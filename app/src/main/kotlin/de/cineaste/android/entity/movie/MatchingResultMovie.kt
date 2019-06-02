package de.cineaste.android.entity.movie

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MatchingResultMovie(
    var id: Long = 0,
    @SerializedName("poster_path")
    var posterPath: String? = "",
    var title: String = "",
    var releaseDate: Date?,
    var voteAverage: Double = 0.toDouble(),
    var runtime: Int = 0,
    val counter: Int
) {
    constructor(movieDto: MovieDto, counter: Int) : this(
        movieDto.id,
        movieDto.posterPath,
        movieDto.title,
        movieDto.releaseDate,
        movieDto.voteAverage,
        movieDto.runtime,
        counter
    )
}