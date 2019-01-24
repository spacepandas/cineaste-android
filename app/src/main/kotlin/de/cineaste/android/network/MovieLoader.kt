package de.cineaste.android.network

import android.content.Context
import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.util.ExtendedDateAwareGson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

interface MovieCallback {
    fun onFailure()
    fun onSuccess(movie: Movie)
}

class MovieLoader(context: Context) {

    private val gson: Gson = ExtendedDateAwareGson.gson
    private val resources: Resources = context.resources

    fun loadLocalizedMovie(movieId: Long, language: Locale, callback: MovieCallback) {
        val client = NetworkClient()

        client.addRequest(NetworkRequest(resources).getMovie(movieId), object : NetworkCallback {
            override fun onFailure() {
                callback.onFailure()
            }

            override fun onSuccess(response: NetworkResponse) {

                callback.onSuccess(parseResponse(response, language))
            }
        })
    }

    private fun parseResponse(response: NetworkResponse, language: Locale): Movie {
        val parser = JsonParser()
        val responseObject = parser.parse(response.responseReader).asJsonObject

        val movie = getMovieFromJson(responseObject)

        val localReleaseDate = getOnlyType3Dates(getReleaseDates(responseObject), language)

        localReleaseDate?.let {
            movie.releaseDate = it.release_date
        }

        return movie
    }

    private fun getMovieFromJson(jsonString: JsonObject): Movie {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        val id = jsonString.get("id").asLong
        val posterPath = jsonString.get("poster_path").asString
        val title = jsonString.get("title").asString
        val runtime = jsonString.get("runtime").asInt
        val voteAverage = jsonString.get("vote_average").asDouble
        val voteCount = jsonString.get("vote_count").asInt
        val description = jsonString.get("overview").asString
        val releaseDate = formatter.parse(jsonString.get("release_date").asString)

        return Movie(
                id,
                posterPath,
                title,
                runtime,
                voteAverage,
                voteCount,
                description,
                releaseDate = releaseDate)
    }

    private fun getReleaseDates(jsonString: JsonObject): HashMap<String, ReleaseDate> {
        val results = jsonString.get("release_dates")
        val releaseDate = gson.fromJson(results, ReleaseDates::class.java)

        val hashMap = HashMap<String, ReleaseDate>()
        for (result in releaseDate.results) {
            hashMap[result.iso_3166_1] = result
        }

        return hashMap
    }

    // Type 3 means only cinema release dates
    private fun getOnlyType3Dates(dates: HashMap<String, ReleaseDate>, language: Locale): ReleaseDateType? {
        val date = dates[language.country]

        date?.let {
            for (release_date in it.release_dates) {
                if (release_date.type == 3) {
                    return release_date
                }
            }
        }

        return null
    }

    data class ReleaseDates(val results: List<ReleaseDate>)

    data class ReleaseDate(
        val iso_3166_1: String,
        val release_dates: List<ReleaseDateType>
    )

    data class ReleaseDateType(
        val certification: String,
        val iso_639_1: String,
        val note: String,
        val release_date: Date,
        val type: Int
    )
}
