package de.cineaste.android.database.room

import androidx.room.*
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.entity.series.EpisodeEntity


const val SELECT_EPISODE = "SELECT * FROM ${BaseDao.EpisodeEntry.TABLE_NAME}"
@Dao
interface EpisodeDao {

    @Query("$SELECT_EPISODE WHERE ${BaseDao.EpisodeEntry.ID} LIKE :episodeId")
    fun getOne(episodeId: Long): EpisodeEntity?

    @Query("$SELECT_EPISODE WHERE ${BaseDao.EpisodeEntry.COLUMN_EPISODE_SEASON_ID} LIKE :seasonId")
    fun getBySeasonId(seasonId: Long): List<EpisodeEntity>

    @Query("$SELECT_EPISODE WHERE ${BaseDao.EpisodeEntry.COLUMN_EPISODE_SERIES_ID} LIKE :seriesId")
    fun getBySeriesId(seriesId: Long): List<EpisodeEntity>

    @Query("$SELECT_EPISODE WHERE ${BaseDao.EpisodeEntry.COLUMN_EPISODE_SERIES_ID} LIKE :seriesId AND ${BaseDao.EpisodeEntry.COLUMN_EPISODE_WATCHED} LIKE :state")
    fun getBySeriesAndWatchedState(seriesId: Long, state: Boolean): List<EpisodeEntity>

    @Query("UPDATE ${BaseDao.EpisodeEntry.TABLE_NAME} SET ${BaseDao.EpisodeEntry.COLUMN_EPISODE_WATCHED} = :state WHERE ${BaseDao.EpisodeEntry.COLUMN_EPISODE_SEASON_ID} LIKE :seasonId")
    fun updateWatchedStateBySeasonId(state: Boolean, seasonId: Long)

    @Query("UPDATE ${BaseDao.EpisodeEntry.TABLE_NAME} SET ${BaseDao.EpisodeEntry.COLUMN_EPISODE_WATCHED} = :state WHERE ${BaseDao.EpisodeEntry.COLUMN_EPISODE_SERIES_ID} LIKE :seriesId")
    fun updateWatchedStateBySeriesId(state: Boolean, seriesId: Long)

    @Query("DELETE FROM ${BaseDao.EpisodeEntry.TABLE_NAME} WHERE ${BaseDao.EpisodeEntry.COLUMN_EPISODE_SERIES_ID} LIKE :seriesId")
    fun deleteBySeriesId(seriesId: Long)

    @Insert
    fun insertAll(episodes: List<EpisodeEntity>)

    @Insert
    fun insert(episode: EpisodeEntity)

    @Update
    fun update(episode: EpisodeEntity)

    @Delete
    fun delete(episode: EpisodeEntity)
}