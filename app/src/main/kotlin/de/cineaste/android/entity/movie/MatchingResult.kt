package de.cineaste.android.entity.movie

import com.google.gson.annotations.SerializedName

data class MatchingResult(
    var id: Long = 0,
    @SerializedName("poster_path")
    var posterPath: String? = "",
    var title: String = "",
    val counter: Int
) {
    constructor(movieDto: MovieDto, counter: Int) : this(
        movieDto.id,
        movieDto.posterPath,
        movieDto.title,
        counter
    )
}