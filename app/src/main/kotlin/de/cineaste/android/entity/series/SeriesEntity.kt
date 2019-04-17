package de.cineaste.android.entity.series

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.cineaste.android.entity.series.SeriesEntity.Companion.TABLE_NAME
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = TABLE_NAME)
data class SeriesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
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
    @ColumnInfo(name = SERIES_WATCHED)
    var isWatched: Boolean = false,
    var listPosition: Int = 0
) {
    companion object {
        const val TABLE_NAME = "series"
        const val ID = "_id"
        const val SERIES_WATCHED = "seriesWatched"
        const val LIST_POSITION = "listPosition"
        private const val NAME = "seriesName"
        private const val VOTE_AVERAGE = "voteAverage"
        private const val VOTE_COUNT = "voteCount"
        private const val DESCRIPTION = "description"
        private const val RELEASE_DATE = "releaseDate"
        private const val IN_PRODUCTION = "inProduction"
        private const val NUMBER_OF_EPISODES = "numberOfEpisodes"
        private const val NUMBER_OF_SEASONS = "numberOfSeasons"
        private const val POSTER_PATH = "posterPath"
        private const val BACKDROP_PATH = "backdropPath"

        const val SQL_CREATE_SERIES_ENTRIES =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY," +
                    "$NAME TEXT," +
                    "$VOTE_AVERAGE REAL," +
                    "$VOTE_COUNT INTEGER," +
                    "$DESCRIPTION TEXT," +
                    "$RELEASE_DATE TEXT," +
                    "$IN_PRODUCTION INTEGER," +
                    "$NUMBER_OF_EPISODES INTEGER," +
                    "$NUMBER_OF_SEASONS INTEGER," +
                    "$POSTER_PATH TEXT," +
                    "$BACKDROP_PATH TEXT," +
                    "$SERIES_WATCHED INTEGER," +
                    "$LIST_POSITION INTEGER" +
                    " )"
    }
}

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