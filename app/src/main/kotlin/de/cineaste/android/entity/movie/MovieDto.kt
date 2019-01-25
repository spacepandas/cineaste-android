package de.cineaste.android.entity.movie

import com.google.gson.annotations.SerializedName

data class MovieDto(
    var id: Long = 0,
    @SerializedName("poster_path")
    var posterPath: String? = "",
    var title: String = ""
) {
    constructor(movie: Movie) : this(movie.id, movie.posterPath, movie.title)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val movieDto = other as MovieDto

        return id == movieDto.id
    }

    override fun hashCode(): Int {
        return (id xor id.ushr(32)).toInt()
    }
}