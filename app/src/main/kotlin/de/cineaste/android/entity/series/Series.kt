package de.cineaste.android.entity.series

import com.google.gson.annotations.SerializedName

import java.util.Date

data class Series(
    var id: Long = 0,
    var name: String = "",
    @SerializedName("vote_average")
    var voteAverage: Double = 0.toDouble(),
    @SerializedName("vote_count")
    var voteCount: Int = 0,
    @SerializedName("overview")
    var description: String? = null,
    @SerializedName("first_air_date")
    var releaseDate: Date? = null,
    @SerializedName("in_production")
    var isInProduction: Boolean = false,
    @SerializedName("number_of_episodes")
    var numberOfEpisodes: Int = 0,
    @SerializedName("number_of_seasons")
    var numberOfSeasons: Int = 0,
    @SerializedName("poster_path")
    var posterPath: String? = null,
    @SerializedName("backdrop_path")
    var backdropPath: String? = null,
    var seasons: List<Season> = listOf(),
    var isWatched: Boolean = false,
    var listPosition: Int = 0
) {
    val currentNumberOfSeason: Int
        get() {
            var lastSeason = Season()
            for (season in seasons) {
                lastSeason = season
                if (!season.isWatched) {
                    return season.seasonNumber
                }
            }

            return lastSeason.seasonNumber
        }

    val currentNumberOfEpisode: Int
        get() {
            var lastSeason = Season()
            for (season in seasons) {
                lastSeason = season
                if (!season.isWatched) {
                    for (episode in season.episodes) {
                        if (!episode.isWatched) {
                            return episode.episodeNumber
                        }
                    }
                }
            }

            return lastSeason.episodeCount
        }
}
