package de.cineaste.android.database.dbHelper

import android.content.Context
import de.cineaste.android.database.room.MovieDao
import de.cineaste.android.db
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.movie.toEntity
import de.cineaste.android.entity.movie.toModel
import de.cineaste.android.fragment.WatchState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MovieDbHelper private constructor(context: Context) {

    private val movieDao: MovieDao = db!!.movieDao()

    fun readMovie(movieId: Long): Movie? {
        var movie: Movie? = null
        GlobalScope.launch { movie = movieDao.getOne(movieId)?.toModel() }

        return movie
    }

    fun readAllMovies(): List<Movie> {
        val movies = mutableListOf<Movie>()

        GlobalScope.launch {
            movies.addAll(movieDao.getAll().map { it.toModel() })
        }
        return movies
    }

    fun readMoviesByWatchStatus(state: WatchState): List<Movie> {
        val movies = mutableListOf<Movie>()

        GlobalScope.launch {
            movies.addAll(movieDao.getAllByWatchState(getStatusFromState(state)).map { it.toModel() })
        }
        return movies
    }

    private fun getStatusFromState(state: WatchState): Boolean {
        return when (state) {
            WatchState.WATCH_STATE -> false
            WatchState.WATCHED_STATE -> true
            else -> false
        }
    }

    fun reorderAlphabetical(state: WatchState): List<Movie> {
        val movies = mutableListOf<Movie>()

        GlobalScope.launch {
            movies.addAll(movieDao.getAllByWatchState(getStatusFromState(state)).map { it.toModel() }
                .sortedBy { it.title })
        }
        return movies
    }

    fun reorderByReleaseDate(state: WatchState): List<Movie> {
        val movies = mutableListOf<Movie>()

        GlobalScope.launch {
            movies.addAll(movieDao.getAllByWatchState(getStatusFromState(state)).map { it.toModel() }
                .sortedBy { it.releaseDate })
        }
        return movies
    }

    fun reorderByRuntime(state: WatchState): List<Movie> {
        val movies = mutableListOf<Movie>()

        GlobalScope.launch {
            movies.addAll(movieDao.getAllByWatchState(getStatusFromState(state)).map { it.toModel() }
                .sortedBy { it.runtime })
        }
        return movies
    }

    fun createOrUpdate(movie: Movie) {
        GlobalScope.launch {
            val existingMovie = movieDao.getOne(movie.id)

            if (existingMovie != null) {
                update(movie, getNewPosition(movie, existingMovie.toModel()))
            } else {
                movieDao.insert(movie.toEntity())
            }
        }
    }

    fun updatePosition(movie: Movie) {
        update(movie, movie.listPosition)
    }

    fun deleteMovieFromWatchlist(movie: Movie) {
        GlobalScope.launch { movieDao.delete(movie.toEntity()) }
    }

    private fun update(movie: Movie, listPosition: Int) {
        GlobalScope.launch { movieDao.update(movie.copy(listPosition = listPosition).toEntity()) }
    }

    private fun getNewPosition(updatedMovie: Movie, oldMovie: Movie): Int {
        var position = 0
        if (updatedMovie.isWatched == oldMovie.isWatched) {
            position = oldMovie.listPosition
        } else {
            GlobalScope.launch {
                position = movieDao.getHighestListPosition(updatedMovie.isWatched)
            }
        }
        return position
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
