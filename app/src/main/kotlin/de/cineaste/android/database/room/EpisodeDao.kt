package de.cineaste.android.database.room

import androidx.room.*
import de.cineaste.android.entity.series.EpisodeEntity


const val SELECT_EPISODE = "SELECT * FROM ${EpisodeEntity.TABLE_NAME}"
@Dao
interface EpisodeDao {

    @Query("$SELECT_EPISODE WHERE ${EpisodeEntity.ID} LIKE :episodeId")
    fun getOne(episodeId: Long): EpisodeEntity?

    @Query("$SELECT_EPISODE WHERE ${EpisodeEntity.SEASON_ID} LIKE :seasonId")
    fun getBySeasonId(seasonId: Long): List<EpisodeEntity>

    @Query("$SELECT_EPISODE WHERE ${EpisodeEntity.SERIES_ID} LIKE :seriesId")
    fun getBySeriesId(seriesId: Long): List<EpisodeEntity>

    @Query("$SELECT_EPISODE WHERE ${EpisodeEntity.SERIES_ID} LIKE :seriesId AND ${EpisodeEntity.EPISODE_WATCHED} LIKE :state")
    fun getBySeriesAndWatchedState(seriesId: Long, state: Boolean): List<EpisodeEntity>

    @Query("UPDATE ${EpisodeEntity.TABLE_NAME} SET ${EpisodeEntity.EPISODE_WATCHED} = :state WHERE ${EpisodeEntity.SEASON_ID} LIKE :seasonId")
    fun updateWatchedStateBySeasonId(state: Boolean, seasonId: Long)

    @Query("UPDATE ${EpisodeEntity.TABLE_NAME} SET ${EpisodeEntity.EPISODE_WATCHED} = :state WHERE ${EpisodeEntity.SERIES_ID} LIKE :seriesId")
    fun updateWatchedStateBySeriesId(state: Boolean, seriesId: Long)

    @Query("DELETE FROM ${EpisodeEntity.TABLE_NAME} WHERE ${EpisodeEntity.SERIES_ID} LIKE :seriesId")
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