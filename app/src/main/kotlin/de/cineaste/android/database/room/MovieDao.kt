package de.cineaste.android.database.room

import androidx.room.*
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.entity.movie.MovieEntity

const val SELECT_MOVIE = "SELECT * FROM ${BaseDao.MovieEntry.TABLE_NAME}"

@Dao
interface MovieDao {

    @Query(SELECT_MOVIE)
    fun getAll(): List<MovieEntity>

    @Query("$SELECT_MOVIE WHERE ${BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED} LIKE :state")
    fun getAllByWatchState(state: Boolean): List<MovieEntity>

    @Query("$SELECT_MOVIE WHERE ${BaseDao.MovieEntry.ID} LIKE :movieId")
    fun getOne(movieId: Long): MovieEntity?

    @Query("SELECT MAX(${BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION}) FROM ${BaseDao.MovieEntry.TABLE_NAME} WHERE ${BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED} LIKE :state")
    fun getHighestListPosition(state: Boolean): Int

    @Insert
    fun insert(movie: MovieEntity)

    @Update
    fun update(movie: MovieEntity)

    @Delete
    fun delete(movie: MovieEntity)

}