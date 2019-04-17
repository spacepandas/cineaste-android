package de.cineaste.android.entity.series

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.cineaste.android.entity.series.SeasonEntity.Companion.TABLE_NAME
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = TABLE_NAME)
data class SeasonEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var id: Long = 0,
    var releaseDate: String? = null,
    var episodeCount: Int = 0,
    var posterPath: String? = null,
    var seasonNumber: Int = 0,
    var seriesId: Long = 0,
    @ColumnInfo(name = WATCHED)
    var isWatched: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "season"
        const val ID = "_id"
        const val WATCHED = "seasonWatched"
        const val SERIES_ID = "seriesId"
        const val SEASON_NUMBER = "seasonNumber"
        private const val RELEASE_DATE = "releaseDate"
        private const val EPISODE_COUNT = "episodenCount"
        private const val POSTER_PATH = "posterPath"

        const val SQL_CREATE_SEASON_ENTRIES =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY," +
                    "$RELEASE_DATE TEXT," +
                    "$EPISODE_COUNT INTEGER," +
                    "$POSTER_PATH TEXT," +
                    "$SEASON_NUMBER INTEGER," +
                    "$WATCHED INTEGER," +
                    "$SERIES_ID  INTEGER" +
                    " )"
    }
}

fun SeasonEntity.toModel(episodes: List<Episode>) =
    Season(
        id,
        releaseDate?.let {
            SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.ENGLISH).parse(it)
        }, episodeCount, posterPath, seasonNumber, seriesId, isWatched, episodes
    )