package de.cineaste.android.entity.series

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.cineaste.android.entity.series.EpisodeEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var id: Long = 0,
    var episodeNumber: Int = 0,
    var name: String = "",
    var description: String? = null,
    var seriesId: Long = 0,
    var seasonId: Long = 0,
    @ColumnInfo(name = EPISODE_WATCHED)
    var isWatched: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "episode"
        const val ID = "_id"
        const val EPISODE_WATCHED = "watched"
        private const val EPISODE_NUMBER = "episodeNumber"
        private const val NAME = "name"
        private const val DESCRIPTION = "description"
        private const val SERIES_ID = "seriesId"
        private const val SEASON_ID = "seasonId"

        const val SQL_CREATE_EPISODE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY," +
                    "$EPISODE_NUMBER INTEGER," +
                    "$NAME TEXT," +
                    "$DESCRIPTION TEXT," +
                    "$SERIES_ID INTEGER," +
                    "$SEASON_ID INTEGER," +
                    "$EPISODE_WATCHED INTEGER" +
                    " )"
    }
}

fun EpisodeEntity.toModel() =
    Episode(
        id, episodeNumber, name, description, seriesId, seasonId, isWatched
    )