package de.cineaste.android.database.room

import androidx.room.*
import de.cineaste.android.entity.series.SeasonEntity

@Dao
interface SeasonDao {

    @Query("SELECT * FROM ${SeasonEntity.TABLE_NAME} WHERE ${SeasonEntity.SERIES_ID} LIKE :seriesId")
    fun getBySeriesId(seriesId: Long): List<SeasonEntity>

    @Query("SELECT * FROM ${SeasonEntity.TABLE_NAME} WHERE ${SeasonEntity.ID} LIKE :seasonId")
    fun getOne(seasonId: Long): SeasonEntity?

    @Query("UPDATE ${SeasonEntity.TABLE_NAME} SET ${SeasonEntity.WATCHED} = :state WHERE ${SeasonEntity.ID} LIKE :seasonId")
    fun updateWatchedStateBySeasonId(state: Boolean, seasonId: Long)

    @Query("UPDATE ${SeasonEntity.TABLE_NAME} SET ${SeasonEntity.WATCHED} = :state WHERE ${SeasonEntity.SERIES_ID} LIKE :seriesId")
    fun updateWatchedStateBySeriesId(state: Boolean, seriesId: Long)

    @Query("DELETE FROM ${SeasonEntity.TABLE_NAME} WHERE ${SeasonEntity.SERIES_ID} LIKE :seriesId")
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