package de.cineaste.android.database.room

import androidx.room.*
import de.cineaste.android.entity.series.SeriesEntity

const val SELECT_SERIES = "SELECT * FROM ${SeriesEntity.TABLE_NAME}"
@Dao
interface SeriesDao {

    @Query(SELECT_SERIES)
    fun getAll(): List<SeriesEntity>

    @Query("$SELECT_SERIES WHERE ${SeriesEntity.SERIES_WATCHED} LIKE :state")
    fun getAllByWatchState(state: Boolean): List<SeriesEntity>

    @Query("$SELECT_SERIES WHERE ${SeriesEntity.ID} LIKE :seriesId")
    fun getOne(seriesId: Long): SeriesEntity

    @Query("SELECT MAX(${SeriesEntity.LIST_POSITION}) FROM ${SeriesEntity.TABLE_NAME} WHERE ${SeriesEntity.SERIES_WATCHED} LIKE :state")
    fun getHighestListPosition(state: Boolean): Int

    @Insert
    fun insert(series: SeriesEntity)

    @Update
    fun update(series: SeriesEntity)

    @Delete
    fun delete(series: SeriesEntity)
}