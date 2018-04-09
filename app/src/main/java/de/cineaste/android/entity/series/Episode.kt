package de.cineaste.android.entity.series

import com.google.gson.annotations.SerializedName

//todo use data class
class Episode {

    var id: Long = 0
    @SerializedName("episode_number")
    var episodeNumber: Int = 0
    var name: String? = null
    @SerializedName("overview")
    var description: String? = null
    var seriesId: Long = 0
    var seasonId: Long = 0
    var isWatched: Boolean = false
}
