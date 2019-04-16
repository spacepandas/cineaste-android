package de.cineaste.android.entity.series

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Season(
    var id: Long = 0,
    @SerializedName("air_date")
    var releaseDate: Date? = null,
    @SerializedName("episode_count")
    var episodeCount: Int = 0,
    @SerializedName("poster_path")
    var posterPath: String? = null,
    @SerializedName("season_number")
    var seasonNumber: Int = 0,
    var seriesId: Long = 0,
    var isWatched: Boolean = false,
    var episodes: List<Episode> = listOf()
)

fun Season.toEntity() =
    SeasonEntity(id, releaseDate?.let {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        simpleDateFormat.format(it)
    }, episodeCount, posterPath, seasonNumber, seriesId, isWatched)