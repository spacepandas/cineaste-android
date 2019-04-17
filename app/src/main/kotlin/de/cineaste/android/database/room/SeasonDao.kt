package de.cineaste.android.database.room

import androidx.room.*
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.entity.series.SeasonEntity

@Dao
interface SeasonDao {

    @Query("SELECT * FROM ${BaseDao.SeasonEntry.TABLE_NAME} WHERE ${BaseDao.SeasonEntry.SERIES_ID} LIKE :seriesId")
    fun getBySeriesId(seriesId: Long): List<SeasonEntity>

    @Query("SELECT * FROM ${BaseDao.SeasonEntry.TABLE_NAME} WHERE ${BaseDao.SeasonEntry.ID} LIKE :seasonId")
    fun getOne(seasonId: Long): SeasonEntity?

    @Query("UPDATE ${BaseDao.SeasonEntry.TABLE_NAME} SET ${BaseDao.SeasonEntry.WATCHED} = :state WHERE ${BaseDao.SeasonEntry.ID} LIKE :seasonId")
    fun updateWatchedStateBySeasonId(state: Boolean, seasonId: Long)

    @Query("UPDATE ${BaseDao.SeasonEntry.TABLE_NAME} SET ${BaseDao.SeasonEntry.WATCHED} = :state WHERE ${BaseDao.SeasonEntry.SERIES_ID} LIKE :seriesId")
    fun updateWatchedStateBySeriesId(state: Boolean, seriesId: Long)

    @Query("DELETE FROM ${BaseDao.SeasonEntry.TABLE_NAME} WHERE ${BaseDao.SeasonEntry.SERIES_ID} LIKE :seriesId")
    fun deleteBySeriesId(seriesId: Long)

    @Insert
    fun insertAll(seasons: List<SeasonEntity>)

    @Insert
    fun insert(season: SeasonEntity)

    @Update
    fun update(season: SeasonEntity)

    @Delete
    fun delete(season: SeasonEntity)
}