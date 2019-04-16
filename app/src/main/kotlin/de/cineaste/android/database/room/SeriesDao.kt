package de.cineaste.android.database.room

import androidx.room.*
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.entity.series.SeriesEntity

const val SELECT_SERIES = "SELECT * FROM ${BaseDao.SeriesEntry.TABLE_NAME}"
@Dao
interface SeriesDao {

    @Query(SELECT_SERIES)
    fun getAll(): List<SeriesEntity>

    @Query("$SELECT_SERIES WHERE ${BaseDao.SeriesEntry.COLUMN_SERIES_SERIES_WATCHED} LIKE :state")
    fun getAllByWatchState(state: Boolean): List<SeriesEntity>

    @Query("$SELECT_SERIES WHERE ${BaseDao.SeriesEntry.ID} LIKE :seriesId")
    fun getOne(seriesId: Long): SeriesEntity

    @Query("SELECT MAX(${BaseDao.SeriesEntry.COLUMN_SERIES_LIST_POSITION}) FROM ${BaseDao.SeriesEntry.TABLE_NAME} WHERE ${BaseDao.SeriesEntry.COLUMN_SERIES_SERIES_WATCHED} LIKE :state")
    fun getHighestListPosition(state: Boolean): Int

    @Insert
    fun insert(series: SeriesEntity)

    @Update
    fun update(series: SeriesEntity)

    @Delete
    fun delete(series: SeriesEntity)
}