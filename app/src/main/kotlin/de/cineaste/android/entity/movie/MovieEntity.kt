package de.cineaste.android.entity.movie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.cineaste.android.entity.movie.MovieEntity.Companion.TABLE_NAME
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = TABLE_NAME)
data class MovieEntity(
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var posterPath: String? = "",
    var title: String = "",
    var runtime: Int = 0,
    var voteAverage: Double = 0.toDouble(),
    var voteCount: Int = 0,
    var description: String = "",
    var watched: Boolean = false,
    var watchedDate: Long? = null,
    var releaseDate: String? = null,
    var listPosition: Int = 0
) {
    companion object {
        const val TABLE_NAME = "movie"
        const val ID = "_id"
        const val RELEASE_DATE = "releaseDate"
        const val LIST_POSITION = "listPosition"
        const val WATCHED = "watched"
        private const val POSTER_PATH = "posterPath"
        private const val RUNTIME = "runtime"
        private const val VOTE_AVERAGE = "voteAverage"
        private const val VOTE_COUNT = "voteCount"
        private const val TITLE = "title"
        private const val DESCRIPTION = "description"
        private const val WATCHED_DATE = "watchedDate"

        const val SQL_CREATE_MOVIE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY," +
                    "$TITLE TEXT," +
                    "$POSTER_PATH TEXT," +
                    "$RUNTIME INTEGER," +
                    "$VOTE_AVERAGE REAL," +
                    "$VOTE_COUNT INTEGER," +
                    "$DESCRIPTION TEXT," +
                    "$WATCHED INTEGER," +
                    "$WATCHED_DATE INTEGER," +
                    "$RELEASE_DATE TEXT," +
                    "$LIST_POSITION INTEGER" +
                    " )"
    }
}

fun MovieEntity.toModel() =
    Movie(
        id,
        posterPath,
        title,
        runtime,
        voteAverage,
        voteCount,
        description,
        watched,
        watchedDate?.let {
            Date(it)
        },
        releaseDate?.let {
            SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.ENGLISH).parse(it)
        },
        listPosition
    )