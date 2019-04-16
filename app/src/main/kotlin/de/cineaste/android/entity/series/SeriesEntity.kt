package de.cineaste.android.entity.series

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.cineaste.android.database.dao.BaseDao
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = BaseDao.SeriesEntry.TABLE_NAME)
data class SeriesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BaseDao.SeriesEntry.ID)
    var id: Long = 0,
    var name: String = "",
    var voteAverage: Double = 0.toDouble(),
    var voteCount: Int = 0,
    var description: String? = null,
    var releaseDate: String? = null,
    var isInProduction: Boolean = false,
    var numberOfEpisodes: Int = 0,
    var numberOfSeasons: Int = 0,
    var posterPath: String? = null,
    var backdropPath: String? = null,
    @ColumnInfo(name = BaseDao.SeriesEntry.COLUMN_SERIES_SERIES_WATCHED)
    var isWatched: Boolean = false,
    var listPosition: Int = 0
)

fun SeriesEntity.toModel(seasons: List<Season>) =
    Series(
        id,
        name,
        voteAverage,
        voteCount,
        description,
        releaseDate?.let {
            SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.ENGLISH).parse(it)
        },
        isInProduction,
        numberOfEpisodes,
        numberOfSeasons,
        posterPath,
        backdropPath,
        seasons,
        isWatched,
        listPosition
    )