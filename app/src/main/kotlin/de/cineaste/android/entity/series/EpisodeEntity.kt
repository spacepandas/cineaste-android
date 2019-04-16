package de.cineaste.android.entity.series

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.cineaste.android.database.dao.BaseDao

@Entity(tableName = BaseDao.EpisodeEntry.TABLE_NAME)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseDao.EpisodeEntry.ID)
    var id: Long = 0,
    var episodeNumber: Int = 0,
    var name: String = "",
    var description: String? = null,
    var seriesId: Long = 0,
    var seasonId: Long = 0,
    var isWatched: Boolean = false
)

fun EpisodeEntity.toModel() =
    Episode(
        id, episodeNumber, name, description, seriesId, seasonId, isWatched
    )