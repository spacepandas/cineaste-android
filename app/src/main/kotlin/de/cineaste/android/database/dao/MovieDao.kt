package de.cineaste.android.database.dao

import android.content.ContentValues
import android.content.Context
import de.cineaste.android.entity.movie.Movie
import java.util.*

class MovieDao private constructor(context: Context) : BaseDao(context) {
    
    fun reorder(statement: String) {
        writeDb.execSQL(statement)
    }

    fun create(movie: Movie) {
        val values = ContentValues()
        values.put(BaseDao.MovieEntry.ID, movie.id)
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_TITLE, movie.title)
        values.put(BaseDao.MovieEntry.COlUMN_POSTER_PATH, movie.posterPath)
        values.put(BaseDao.MovieEntry.COLUMN_RUNTIME, movie.runtime)
        values.put(BaseDao.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage)
        values.put(BaseDao.MovieEntry.COLUMN_VOTE_COUNT, movie.voteCount)
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_DESCRIPTION, movie.description)
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED, if (movie.isWatched) 1 else 0)
        if (movie.watchedDate != null) {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED_DATE, movie.watchedDate!!.time)
        }
        if (movie.releaseDate !=
                null) {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, sdf.format(movie.releaseDate))
        } else {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "")
        }

        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION, getHighestListPosition(movie.isWatched) + 1)

        writeDb.insert(BaseDao.MovieEntry.TABLE_NAME, null, values)
    }

    fun read(selection: String?, selectionArgs: Array<String>?, orderBy: String?): List<Movie> {
        val movies = ArrayList<Movie>()

        val projection = arrayOf(BaseDao.MovieEntry.ID, BaseDao.MovieEntry.COLUMN_MOVIE_TITLE, BaseDao.MovieEntry.COlUMN_POSTER_PATH, BaseDao.MovieEntry.COLUMN_RUNTIME, BaseDao.MovieEntry.COLUMN_VOTE_AVERAGE, BaseDao.MovieEntry.COLUMN_VOTE_COUNT, BaseDao.MovieEntry.COLUMN_MOVIE_DESCRIPTION, BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED, BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED_DATE, BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION)

        val c = readDb.query(
                BaseDao.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null, null,
                orderBy, null)

        if (c.moveToFirst()) {
            do {
                val currentMovie = Movie()
                currentMovie.id = c.getLong(c.getColumnIndexOrThrow(BaseDao.MovieEntry.ID))
                currentMovie.title = c.getString(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_MOVIE_TITLE))
                currentMovie.posterPath = c.getString(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COlUMN_POSTER_PATH))
                currentMovie.runtime = c.getInt(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_RUNTIME))
                currentMovie.voteAverage = c.getDouble(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_VOTE_AVERAGE))
                currentMovie.voteCount = c.getInt(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_VOTE_COUNT))
                currentMovie.description = c.getString(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_MOVIE_DESCRIPTION))
                currentMovie.isWatched = c.getInt(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED)) > 0
                currentMovie.watchedDate = Date(c.getLong(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED_DATE)))

                try {
                    currentMovie.releaseDate = sdf.parse(c.getString(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE)))
                } catch (ex: Exception) {
                    currentMovie.releaseDate = null
                }

                currentMovie.listPosition = c.getInt(c.getColumnIndexOrThrow(BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION))

                movies.add(currentMovie)
            } while (c.moveToNext())
        }
        c.close()
        return movies
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>) {
        writeDb.update(BaseDao.MovieEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    fun delete(id: Long) {
        writeDb.delete(BaseDao.MovieEntry.TABLE_NAME, BaseDao.MovieEntry.ID + " = ?", arrayOf(id.toString() + ""))
    }

    fun getHighestListPosition(watchState: Boolean): Int {
        var highestPos = 0

        val projection = arrayOf("MAX(" + BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION + ") AS POS")

        val selection = BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = ?"

        val selectionArg: String
        if (watchState) {
            selectionArg = "1"
        } else {
            selectionArg = "0"
        }

        val c = writeDb.query(
                BaseDao.MovieEntry.TABLE_NAME,
                projection,
                selection,
                arrayOf(selectionArg), null, null, null)
        if (c.moveToFirst()) {
            do {
                highestPos = c.getInt(c.getColumnIndex("POS"))
            } while (c.moveToNext())
        }

        c.close()
        return highestPos
    }

    companion object {
        private var mInstance: MovieDao? = null

        fun getInstance(context: Context): MovieDao {
            if (mInstance == null) {
                mInstance = MovieDao(context)
            }

            return mInstance!!
        }
    }
}
