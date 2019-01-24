package de.cineaste.android.entity.movie

import com.google.gson.annotations.SerializedName

import java.util.Date

data class Movie(
    var id: Long = 0,
    @SerializedName("poster_path")
    var posterPath: String? = "",
    var title: String = "",
    var runtime: Int = 0,
    @SerializedName("vote_average")
    var voteAverage: Double = 0.toDouble(),
    @SerializedName("vote_count")
    var voteCount: Int = 0,
    @SerializedName("overview")
    var description: String = "",
    private var watched: Boolean = false,
    var watchedDate: Date? = null,
    @SerializedName("release_date")
    var releaseDate: Date? = null,
    var listPosition: Int = 0

) : Comparable<Movie> {
    var isWatched: Boolean
        get() = watched
        set(watched) {
            this.watched = watched
            if (watched && this.watchedDate == null) {
                this.watchedDate = Date()
            } else {
                this.watchedDate = null
            }
        }

    override fun compareTo(other: Movie): Int {
        return this.id.compareTo(other.id)
    }
}