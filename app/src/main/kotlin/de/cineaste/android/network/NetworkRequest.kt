package de.cineaste.android.network

import android.content.res.Resources

import java.util.Locale

import de.cineaste.android.R
import okhttp3.Request

class NetworkRequest(resources: Resources) {

    private val baseUrl = "https://api.themoviedb.org/3"

    private val defaultLanguage = Locale.getDefault()
    private val staticQueryParams: String = "language=${defaultLanguage.language}" +
            "&api_key=${resources.getString(R.string.movieKey)}"
    private val region = "&region=${defaultLanguage.country}&with_release_type=3"
    private val appendReleaseDate = "&append_to_response=release_dates"

    private val requestBuilder: Request.Builder = Request.Builder()

    val upcomingMovies: NetworkRequest
        get() {
            this.requestBuilder.url("$baseUrl/movie/upcoming?$staticQueryParams$region")
            this.requestBuilder.header("accept", "application/json")
            return this
        }

    val popularSeries: NetworkRequest
        get() {
            this.requestBuilder.url("$baseUrl/tv/popular?$staticQueryParams")
            this.requestBuilder.header("accept", "application/json")
            return this
        }

    fun getMovie(movieID: Long): NetworkRequest {
        this.requestBuilder.url("$baseUrl/movie/$movieID?$staticQueryParams$appendReleaseDate")
        this.requestBuilder.header("accept", "application/json")
        return this
    }

    fun searchMovie(query: String): NetworkRequest {
        this.requestBuilder.url("$baseUrl/search/movie?query=$query&$staticQueryParams$region")
        this.requestBuilder.header("accept", "application/json")
        return this
    }

    fun getSeries(seriesId: Long): NetworkRequest {
        this.requestBuilder.url("$baseUrl/tv/$seriesId?$staticQueryParams")
        this.requestBuilder.header("accept", "application/json")
        return this
    }

    fun searchSeries(query: String): NetworkRequest {
        this.requestBuilder.url("$baseUrl/search/tv?query=$query&$staticQueryParams")
        this.requestBuilder.header("accept", "application/json")
        return this
    }

    fun getSeason(seriesId: Long, seasonNumber: Int): NetworkRequest {
        this.requestBuilder.url("$baseUrl/tv/$seriesId/season/$seasonNumber?$staticQueryParams")
        this.requestBuilder.header("accept", "application/json")
        return this
    }

    internal fun buildRequest(): Request {
        return this.requestBuilder.build()
    }
}