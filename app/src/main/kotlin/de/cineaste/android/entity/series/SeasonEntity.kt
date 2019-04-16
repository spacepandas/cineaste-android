package de.cineaste.android.entity.series

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.cineaste.android.database.dao.BaseDao
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = BaseDao.SeasonEntry.TABLE_NAME)
data class SeasonEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseDao.SeasonEntry.ID)
    var id: Long = 0,
    var releaseDate: String? = null,
    var episodeCount: Int = 0,
    var posterPath: String? = null,
    var seasonNumber: Int = 0,
    var seriesId: Long = 0,
    @ColumnInfo(name = BaseDao.SeasonEntry.COLUMN_SEASON_WATCHED)
    var isWatched: Boolean = false
)

fun SeasonEntity.toModel(episodes: List<Episode>) =
    Season(
        id,
        releaseDate?.let {
            SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.ENGLISH).parse(it)
        }, episodeCount, posterPath, seasonNumber, seriesId, isWatched, episodes
    )