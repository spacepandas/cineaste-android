package de.cineaste.android.network

import android.content.Context
import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.util.ExtendedDateAwareGson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
        val responseObject = JsonParser.parseReader(response.responseReader).asJsonObject
        val movie = getMovieFromJson(responseObject)

        val localReleaseDate = getOnlyType3Dates(getReleaseDates(responseObject), language)

        localReleaseDate?.let {
            movie.releaseDate = it.release_date
        }

        return movie
    }

    private fun getMovieFromJson(jsonString: JsonObject): Movie {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        val id = try {
            jsonString.get("id").asLong
        } catch (ex: UnsupportedOperationException) {
            0L
        }
        val posterPath = try {
            jsonString.get("poster_path").asString
        } catch (ex: UnsupportedOperationException) {
            ""
        }
        val title = try {
            jsonString.get("title").asString
        } catch (ex: UnsupportedOperationException) {
            ""
        }
        val runtime = try {
            jsonString.get("runtime").asInt
        } catch (ex: UnsupportedOperationException) {
            0
        }
        val voteAverage = try {
            jsonString.get("vote_average").asDouble
        } catch (ex: UnsupportedOperationException) {
            0.0
        }
        val voteCount = try {
            jsonString.get("vote_count").asInt
        } catch (ex: UnsupportedOperationException) {
            0
        }
        val description = try {
            jsonString.get("overview").asString
        } catch (ex: UnsupportedOperationException) {
            ""
        }
        val releaseDate = try {
            formatter.parse(jsonString.get("release_date").asString)
        } catch (ex: UnsupportedOperationException) {
            Date()
        }

        return Movie(
            id,
            posterPath,
            title,
            runtime,
            voteAverage,
            voteCount,
            description,
            releaseDate = releaseDate
        )
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
    private fun getOnlyType3Dates(
        dates: HashMap<String, ReleaseDate>,
        language: Locale
    ): ReleaseDateType? {
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
