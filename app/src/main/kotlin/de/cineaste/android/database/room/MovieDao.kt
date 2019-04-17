package de.cineaste.android.database.room

import androidx.room.*
import de.cineaste.android.entity.movie.MovieEntity

const val SELECT_MOVIE = "SELECT * FROM ${MovieEntity.TABLE_NAME}"

@Dao
interface MovieDao {

    @Query(SELECT_MOVIE)
    fun getAll(): List<MovieEntity>

    @Query("$SELECT_MOVIE WHERE ${MovieEntity.WATCHED} LIKE :state")
    fun getAllByWatchState(state: Boolean): List<MovieEntity>

    @Query("$SELECT_MOVIE WHERE ${MovieEntity.ID} LIKE :movieId")
    fun getOne(movieId: Long): MovieEntity?

    @Query("SELECT MAX(${MovieEntity.LIST_POSITION}) FROM ${MovieEntity.TABLE_NAME} WHERE ${MovieEntity.WATCHED} LIKE :state")
    fun getHighestListPosition(state: Boolean): Int

    @Insert
    fun insert(movie: MovieEntity)

    @Update
    fun update(movie: MovieEntity)

    @Delete
    fun delete(movie: MovieEntity)

}