package de.cineaste.android.database.dbHelper

import android.content.ContentValues
import android.content.Context
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.dao.MovieDao
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.fragment.WatchState
import java.text.SimpleDateFormat
import java.util.Locale

class MovieDbHelper private constructor(context: Context) {

    private val movieDao: MovieDao = MovieDao.getInstance(context)
    private val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    fun readMovie(movieId: Long): Movie? {
        val selection = BaseDao.MovieEntry.ID + " = ?"
        val selectionArgs = arrayOf(java.lang.Long.toString(movieId))

        val movies = movieDao.read(selection, selectionArgs, null)

        return if (movies.isEmpty()) null else movies[0]
    }

    fun readAllMovies(): List<Movie> {
        return movieDao.read(null, null, null)
    }

    fun readMoviesByWatchStatus(state: WatchState): List<Movie> {
        val selectionArg = getSelectionArgs(state)
        val selection = BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = ?"
        val selectionArgs = arrayOf(selectionArg)

        return movieDao.read(
            selection,
            selectionArgs,
            BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION + " ASC"
        )
    }

    fun reorderAlphabetical(state: WatchState): List<Movie> {
        return reorder(state, BaseDao.MovieEntry.COLUMN_MOVIE_TITLE)
    }

    fun reorderByReleaseDate(state: WatchState): List<Movie> {
        return reorder(state, BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE)
    }

    fun reorderByRuntime(state: WatchState): List<Movie> {
        return reorder(state, BaseDao.MovieEntry.COLUMN_RUNTIME)
    }

    private fun reorder(state: WatchState, orderBy: String): List<Movie> {
        val sql = "UPDATE " + BaseDao.MovieEntry.TABLE_NAME +
                " SET " + BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION + " = ( " +
                "SELECT COUNT(*) " +
                "FROM " + BaseDao.MovieEntry.TABLE_NAME + " AS t2 " +
                "WHERE t2." + orderBy + " <= " + BaseDao.MovieEntry.TABLE_NAME + "." + orderBy +
                " ) " +
                "WHERE " + BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = " + getSelectionArgs(state)
        movieDao.reorder(sql)

        return readMoviesByWatchStatus(state)
    }

    private fun getSelectionArgs(state: WatchState): String {
        return if (state == WatchState.WATCH_STATE) "0" else "1"
    }

    fun createOrUpdate(movie: Movie) {
        val selection = BaseDao.MovieEntry.ID + " = ?"
        val selectionArgs = arrayOf(java.lang.Long.toString(movie.id))
        val movieList = movieDao.read(selection, selectionArgs, null)

        if (movieList.isNotEmpty()) {
            update(movie, getNewPosition(movie, movieList[0]))
        } else {
            movieDao.create(movie)
        }
    }

    fun updatePosition(movie: Movie) {
        update(movie, movie.listPosition)
    }

    fun deleteMovieFromWatchlist(movie: Movie) {
        movieDao.delete(movie.id)
    }

    private fun update(movie: Movie, listPosition: Int) {
        val values = ContentValues()
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED, if (movie.isWatched) 1 else 0)
        movie.watchedDate?.let {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED_DATE, it.time)
        }
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_TITLE, movie.title)
        values.put(BaseDao.MovieEntry.COLUMN_RUNTIME, movie.runtime)
        values.put(BaseDao.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage)
        values.put(BaseDao.MovieEntry.COLUMN_VOTE_COUNT, movie.voteCount)
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_DESCRIPTION, movie.description)
        values.put(BaseDao.MovieEntry.COlUMN_POSTER_PATH, movie.posterPath)
        if (movie.releaseDate != null) {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, sdf.format(movie.releaseDate))
        } else {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "")
        }
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION, listPosition)
        val selection = BaseDao.MovieEntry.ID + " LIKE ?"
        val where = arrayOf(movie.id.toString())

        movieDao.update(values, selection, where)
    }

    private fun getNewPosition(updatedMovie: Movie, oldMovie: Movie): Int {
        return if (updatedMovie.isWatched == oldMovie.isWatched) {
            oldMovie.listPosition
        } else movieDao.getHighestListPosition(updatedMovie.isWatched)
    }

    companion object {

        private var instance: MovieDbHelper? = null

        fun getInstance(context: Context): MovieDbHelper {
            if (instance == null) {
                instance = MovieDbHelper(context)
            }
            return instance!!
        }
    }
}
