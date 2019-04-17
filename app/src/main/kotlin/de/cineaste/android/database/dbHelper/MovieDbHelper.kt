package de.cineaste.android.database.dbHelper

import android.content.Context
import de.cineaste.android.database.CineasteDb
import de.cineaste.android.database.room.MovieDao
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.movie.toEntity
import de.cineaste.android.entity.movie.toModel
import de.cineaste.android.fragment.WatchState

class MovieDbHelper private constructor(context: Context) {

    private val movieDao: MovieDao = CineasteDb.getDatabase(context).movieDao()

    fun readMovie(movieId: Long): Movie? {
        return movieDao.getOne(movieId)?.toModel()
    }

    fun readAllMovies(): List<Movie> {

        return movieDao.getAll().map { it.toModel() }
    }

    fun readMoviesByWatchStatus(state: WatchState): List<Movie> {
        return movieDao.getAllByWatchState(getStatusFromState(state)).map { it.toModel() }
    }

    private fun getStatusFromState(state: WatchState): Boolean {
        return when (state) {
            WatchState.WATCH_STATE -> false
            WatchState.WATCHED_STATE -> true
            else -> false
        }
    }

    fun reorderAlphabetical(state: WatchState): List<Movie> {
        return movieDao.getAllByWatchState(getStatusFromState(state)).map { it.toModel() }
            .sortedBy { it.title }
    }

    fun reorderByReleaseDate(state: WatchState): List<Movie> {
        return movieDao.getAllByWatchState(getStatusFromState(state)).map { it.toModel() }
            .sortedBy { it.releaseDate }
    }

    fun reorderByRuntime(state: WatchState): List<Movie> {
        return movieDao.getAllByWatchState(getStatusFromState(state)).map { it.toModel() }
            .sortedBy { it.runtime }
    }

    fun createOrUpdate(movie: Movie) {
        val existingMovie = movieDao.getOne(movie.id)

        if (existingMovie != null) {
            update(movie, getNewPosition(movie, existingMovie.toModel()))
        } else {
            movieDao.insert(movie.toEntity())
        }
    }

    fun updatePosition(movie: Movie) {
        update(movie, movie.listPosition)
    }

    fun deleteMovieFromWatchlist(movie: Movie) {
        movieDao.delete(movie.toEntity())
    }

    private fun update(movie: Movie, listPosition: Int) {
        movieDao.update(movie.copy(listPosition = listPosition).toEntity())
    }

    private fun getNewPosition(updatedMovie: Movie, oldMovie: Movie): Int {
        return if (updatedMovie.isWatched == oldMovie.isWatched) {
            oldMovie.listPosition
        } else movieDao.getHighestListPosition(updatedMovie.isWatched)
    }

    companion object {
        @Volatile
        private var instance: MovieDbHelper? = null

        fun getInstance(context: Context): MovieDbHelper {
            return instance ?: synchronized(this) {
                val dbHelper = MovieDbHelper(context)
                this.instance = dbHelper
                return dbHelper
            }
        }
    }
}
