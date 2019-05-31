package de.cineaste.android.database.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

import java.text.SimpleDateFormat
import java.util.Locale

import de.cineaste.android.util.Constants

abstract class BaseDao protected constructor(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    protected val readDb: SQLiteDatabase = readableDatabase
    protected val writeDb: SQLiteDatabase = writableDatabase
    protected val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    abstract class UserEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "user"
            const val ID = "_id"
            const val COLUMN_USER_NAME = "userName"
        }
    }

    abstract class MovieEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "movie"
            const val ID = "_id"
            const val COlUMN_POSTER_PATH = "posterPath"
            const val COLUMN_RUNTIME = "runtime"
            const val COLUMN_VOTE_AVERAGE = "voteAverage"
            const val COLUMN_VOTE_COUNT = "voteCount"
            const val COLUMN_MOVIE_TITLE = "title"
            const val COLUMN_MOVIE_DESCRIPTION = "description"
            const val COLUMN_MOVIE_WATCHED = "watched"
            const val COLUMN_MOVIE_WATCHED_DATE = "watchedDate"
            const val COLUMN_MOVIE_RELEASE_DATE = "releaseDate"
            const val COLUMN_MOVIE_LIST_POSITION = "listPosition"
        }
    }

    abstract class SeriesEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "series"
            const val ID = "_id"
            const val COLUMN_SERIES_NAME = "seriesName"
            internal const val COLUMN_SERIES_VOTE_AVERAGE = "voteAverage"
            internal const val COLUMN_SERIES_VOTE_COUNT = "voteCount"
            internal const val COLUMN_SERIES_DESCRIPTION = "description"
            const val COLUMN_SERIES_RELEASE_DATE = "releaseDate"
            internal const val COLUMN_SERIES_IN_PRODUCTION = "inProduction"
            internal const val COLUMN_SERIES_NUMBER_OF_EPISODES = "numberOfEpisodes"
            internal const val COLUMN_SERIES_NUMBER_OF_SEASONS = "numberOfSeasons"
            internal const val COLUMN_SERIES_POSTER_PATH = "posterPath"
            const val COLUMN_SERIES_SERIES_WATCHED = "seriesWatched"
            const val COLUMN_SERIES_LIST_POSITION = "listPosition"
            internal const val COLUMN_SERIES_BACKDROP_PATH = "backdropPath"
        }
    }

    abstract class SeasonEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "season"
            const val ID = "_id"
            const val COLUMN_SEASON_SERIES_ID = "seriesId"
            const val COLUMN_SEASON_SEASON_NUMBER = "seasonNumber"
            internal const val COLUMN_SEASON_RELEASE_DATE = "releaseDate"
            internal const val COLUMN_SEASON_EPISODE_COUNT = "episodenCount"
            internal const val COLUMN_SEASON_POSTER_PATH = "posterPath"
            const val COLUMN_SEASON_WATCHED = "seasonWatched"
        }
    }

    abstract class EpisodeEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "episode"
            const val ID = "_id"
            internal const val COLUMN_EPISODE_EPISODE_NUMBER = "episodeNumber"
            internal const val COLUMN_EPISODE_NAME = "name"
            internal const val COLUMN_EPISODE_DESCRIPTION = "description"
            const val COLUMN_EPISODE_SERIES_ID = "seriesId"
            const val COLUMN_EPISODE_SEASON_ID = "seasonId"
            const val COLUMN_EPISODE_WATCHED = "watched"
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_USER_ENTRIES)
        db.execSQL(SQL_CREATE_MOVIE_ENTRIES)
        db.execSQL(SQL_CREATE_SERIES_ENTRIES)
        db.execSQL(SQL_CREATE_SEASON_ENTRIES)
        db.execSQL(SQL_CREATE_EPISODE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + MovieEntry.TABLE_NAME + " ADD COLUMN " + MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " " + TEXT_TYPE + ";")
        }

        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE " + MovieEntry.TABLE_NAME + " ADD COLUMN " + MovieEntry.COLUMN_MOVIE_LIST_POSITION + " " + INTEGER_TYPE + ";")
        }

        if (oldVersion < 4) {
            db.execSQL(SQL_CREATE_SERIES_ENTRIES)
            db.execSQL(SQL_CREATE_SEASON_ENTRIES)
            db.execSQL(SQL_CREATE_EPISODE_ENTRIES)
        }
    }

    companion object {

        private const val TEXT_TYPE = " TEXT"
        private const val INTEGER_TYPE = " INTEGER"
        private const val REAL_TYPE = " REAL"
        private const val COMMA_SEP = ","
        private const val SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME + " (" +
                    UserEntry.ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    UserEntry.COLUMN_USER_NAME + TEXT_TYPE +
                    " )"
        private const val SQL_CREATE_MOVIE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry.ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    MovieEntry.COLUMN_MOVIE_TITLE + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COlUMN_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_RUNTIME + INTEGER_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_VOTE_AVERAGE + REAL_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_VOTE_COUNT + INTEGER_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_MOVIE_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_MOVIE_WATCHED + INTEGER_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_MOVIE_WATCHED_DATE + INTEGER_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_MOVIE_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    MovieEntry.COLUMN_MOVIE_LIST_POSITION + INTEGER_TYPE +
                    " )"
        private const val SQL_CREATE_SERIES_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + SeriesEntry.TABLE_NAME + " (" +
                    SeriesEntry.ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_NAME + TEXT_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE + REAL_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_VOTE_COUNT + INTEGER_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_IN_PRODUCTION + INTEGER_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES + INTEGER_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS + INTEGER_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_BACKDROP_PATH + TEXT_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + INTEGER_TYPE + COMMA_SEP +
                    SeriesEntry.COLUMN_SERIES_LIST_POSITION + INTEGER_TYPE +
                    " )"
        private const val SQL_CREATE_SEASON_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + SeasonEntry.TABLE_NAME + " (" +
                    SeasonEntry.ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    SeasonEntry.COLUMN_SEASON_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                    SeasonEntry.COLUMN_SEASON_EPISODE_COUNT + INTEGER_TYPE + COMMA_SEP +
                    SeasonEntry.COLUMN_SEASON_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
                    SeasonEntry.COLUMN_SEASON_SEASON_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    SeasonEntry.COLUMN_SEASON_WATCHED + INTEGER_TYPE + COMMA_SEP +
                    SeasonEntry.COLUMN_SEASON_SERIES_ID + INTEGER_TYPE + " )"

        private const val SQL_CREATE_EPISODE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + EpisodeEntry.TABLE_NAME + " (" +
                    EpisodeEntry.ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER + INTEGER_TYPE + COMMA_SEP +
                    EpisodeEntry.COLUMN_EPISODE_NAME + TEXT_TYPE + COMMA_SEP +
                    EpisodeEntry.COLUMN_EPISODE_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    EpisodeEntry.COLUMN_EPISODE_SERIES_ID + INTEGER_TYPE + COMMA_SEP +
                    EpisodeEntry.COLUMN_EPISODE_SEASON_ID + INTEGER_TYPE + COMMA_SEP +
                    EpisodeEntry.COLUMN_EPISODE_WATCHED + INTEGER_TYPE +
                    " )"

        private const val DATABASE_VERSION = Constants.DATABASE_VERSION
        private const val DATABASE_NAME = Constants.DATABASE_NAME
    }
}
