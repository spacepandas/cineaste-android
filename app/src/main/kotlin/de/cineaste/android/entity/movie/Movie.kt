package de.cineaste.android.entity.movie

import com.google.gson.annotations.SerializedName

import java.util.Date
//todo use data class
class Movie : MovieDto, Comparable<Movie> {

    var runtime: Int = 0
    @SerializedName("vote_average")
    var voteAverage: Double = 0.toDouble()
    @SerializedName("vote_count")
    var voteCount: Int = 0
    @SerializedName("overview")
    var description: String? = null
    private var watched: Boolean = false
    var watchedDate: Date? = null
    @SerializedName("release_date")
    var releaseDate: Date? = null
    var listPosition: Int = 0

    var isWatched: Boolean
        get() = watched
        set(watched) {
            this.watched = watched
            if (watched && this.watchedDate == null) {
                this.watchedDate = Date()
            }
        }

    constructor() {
        this.watched = false
    }

    constructor(
            id: Long,
            posterPath: String,
            runtime: Int,
            title: String,
            voteAverage: Double,
            description: String,
            voteCount: Int) : super(id, posterPath, title) {
        this.runtime = runtime
        this.voteAverage = voteAverage
        this.voteCount = voteCount
        this.description = description
        this.watched = false
    }

    override fun compareTo(other: Movie): Int {
        return this.title!!.compareTo(other.title!!)
    }
}
