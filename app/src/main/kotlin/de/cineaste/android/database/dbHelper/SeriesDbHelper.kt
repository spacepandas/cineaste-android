package de.cineaste.android.database.dbHelper

import android.content.Context
import java.util.ArrayList
import de.cineaste.android.database.dao.BaseDao.EpisodeEntry
import de.cineaste.android.database.dao.BaseDao.SeasonEntry
import de.cineaste.android.database.dao.BaseDao.SeriesEntry
import de.cineaste.android.database.dao.EpisodeDao
import de.cineaste.android.database.dao.SeasonDao
import de.cineaste.android.database.dao.SeriesDao
import de.cineaste.android.entity.series.Episode
import de.cineaste.android.entity.series.Season
import de.cineaste.android.entity.series.Series
import de.cineaste.android.fragment.WatchState

class SeriesDbHelper private constructor(context: Context) {

    private val seriesDao: SeriesDao = SeriesDao.getInstance(context)
    private val seasonDao: SeasonDao = SeasonDao.getInstance(context)
    private val episodeDao: EpisodeDao = EpisodeDao.getInstance(context)

    val allSeries: List<Series>
        get() {
            val seriesList = seriesDao.read(null, null, null)

            for (series in seriesList) {
                loadRemainingInformation(series)
            }

            return seriesList
        }

    fun getSeriesById(seriesId: Long): Series? {
        val selection = SeriesEntry.ID + " = ?"
        val selectionArgs = arrayOf(seriesId.toString())

        val seriesList = seriesDao.read(selection, selectionArgs, null)

        if (seriesList.isEmpty()) {
            return null
        }

        val series = seriesList[0]
        loadRemainingInformation(series)

        return series
    }

    fun getSeriesByWatchedState(watchedState: WatchState): List<Series> {
        val selectionArg = getSelectionArgs(watchedState)
        val selection = SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + " = ?"
        val selectionArgs = arrayOf(selectionArg)

        val seriesList = seriesDao.read(
            selection,
            selectionArgs,
            SeriesEntry.COLUMN_SERIES_LIST_POSITION + " ASC"
        )

        for (series in seriesList) {
            loadRemainingInformation(series)
        }

        return seriesList
    }

    fun getUnWatchedEpisodesOfSeries(seriesId: Long): List<Episode> {
        val selection =
            EpisodeEntry.COLUMN_EPISODE_SERIES_ID + " = ? AND " + EpisodeEntry.COLUMN_EPISODE_WATCHED + " = 0"
        val selectionArgs = arrayOf(seriesId.toString())

        return episodeDao.read(selection, selectionArgs)
    }

    fun getEpisodesBySeasonId(seasonId: Long): List<Episode> {
        val selection = EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = ?"
        val selectionArgs = arrayOf(seasonId.toString())

        return episodeDao.read(selection, selectionArgs)
    }

    fun reorderAlphabetical(state: WatchState): List<Series> {
        return reorder(state, SeriesEntry.COLUMN_SERIES_NAME)
    }

    fun reorderByReleaseDate(state: WatchState): List<Series> {
        return reorder(state, SeriesEntry.COLUMN_SERIES_RELEASE_DATE)
    }

    private fun reorder(state: WatchState, orderBy: String): List<Series> {
        val sql = "UPDATE " + SeriesEntry.TABLE_NAME +
                " SET " + SeriesEntry.COLUMN_SERIES_LIST_POSITION + " = ( " +
                "SELECT COUNT(*) " +
                "FROM " + SeriesEntry.TABLE_NAME + " AS t2 " +
                "WHERE t2." + orderBy + " <= " + SeriesEntry.TABLE_NAME + "." + orderBy +
                " ) " +
                "WHERE " + SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + " = " + getSelectionArgs(state)
        seriesDao.reorder(sql)

        return getSeriesByWatchedState(state)
    }

    private fun loadRemainingInformation(series: Series) {
        val seasons = readSeasonsBySeriesId(series.id)

        for (season in seasons) {
            season.episodes = readEpisodesBySeasonId(season.id)
        }

        series.seasons = seasons
    }

    private fun getSeasonById(seasonId: Long): Season? {
        val selection = SeasonEntry.ID + " = ?"
        val selectionArgs = arrayOf(seasonId.toString())

        val seasonList = seasonDao.read(selection, selectionArgs)

        return if (seasonList.isEmpty()) null else seasonList[0]
    }

    private fun readSeasonsBySeriesId(seriesId: Long): List<Season> {
        val selection = SeasonEntry.COLUMN_SEASON_SERIES_ID + " = ?"
        val selectionArgs = arrayOf(seriesId.toString())

        return seasonDao.read(selection, selectionArgs)
    }

    private fun readEpisodesBySeasonId(seasonId: Long): List<Episode> {
        val selection = EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = ?"
        val selectionArgs = arrayOf(seasonId.toString())

        return episodeDao.read(selection, selectionArgs)
    }

    fun addToWatchList(series: Series) {
        addToList(series, false)
    }

    fun addToHistory(series: Series) {
        addToList(series, true)
    }

    private fun addToList(series: Series, watchState: Boolean) {
        series.isWatched = watchState
        series.listPosition = seriesDao.getHighestListPosition(watchState)
        seriesDao.create(series)
        for (season in series.seasons) {
            season.isWatched = watchState
            seasonDao.create(season, series.id)
            for (episode in season.episodes) {
                episode.isWatched = watchState
                episodeDao.create(episode, series.id, season.id)
            }
        }
    }

    fun moveToWatchList(series: Series) {
        moveBetweenLists(series, false)
    }

    fun moveToHistory(series: Series) {
        moveBetweenLists(series, true)
    }

    fun toggleSeason(seasonId: Long) {
        getSeasonById(seasonId)?.let { season ->
            val watchState = !season.isWatched
            val updateEpisodesSql = "UPDATE " + EpisodeEntry.TABLE_NAME +
                    " SET " + EpisodeEntry.COLUMN_EPISODE_WATCHED + " = " + (if (watchState) 1 else 0).toString() +
                    " WHERE " + EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = " + seasonId

            val updateSeasonsSql = "UPDATE " + SeasonEntry.TABLE_NAME +
                    " SET " + SeasonEntry.COLUMN_SEASON_WATCHED + " = " + (if (watchState) 1 else 0).toString() +
                    " WHERE " + SeasonEntry.ID + " = " + seasonId

            seasonDao.executeCustomSql(updateSeasonsSql)
            episodeDao.executeCustomSql(updateEpisodesSql)
        }
    }

    private fun moveBetweenLists(series: Series, watchState: Boolean) {
        val updateEpisodesSql = "UPDATE " + EpisodeEntry.TABLE_NAME +
                " SET " + EpisodeEntry.COLUMN_EPISODE_WATCHED + " = " + (if (watchState) 1 else 0).toString() +
                " WHERE " + EpisodeEntry.COLUMN_EPISODE_SERIES_ID + " = " + series.id

        val updateSeasonsSql = "UPDATE " + SeasonEntry.TABLE_NAME +
                " SET " + SeasonEntry.COLUMN_SEASON_WATCHED + " = " + (if (watchState) 1 else 0).toString() +
                " WHERE " + SeasonEntry.COLUMN_SEASON_SERIES_ID + " = " + series.id

        series.isWatched = watchState

        seriesDao.update(series, seriesDao.getHighestListPosition(watchState))
        seasonDao.executeCustomSql(updateSeasonsSql)
        episodeDao.executeCustomSql(updateEpisodesSql)
    }

    fun moveBackToWatchList(series: Series, prevSeason: Int, prevEpisode: Int) {
        moveBackToList(series, prevSeason, prevEpisode, false)
    }

    fun moveBackToHistory(series: Series, prevSeason: Int, prevEpisode: Int) {
        moveBackToList(series, prevSeason, prevEpisode, true)
    }

    private fun moveBackToList(
        series: Series,
        prevSeason: Int,
        prevEpisode: Int,
        watchState: Boolean
    ) {
        moveBetweenLists(series, watchState)
        for (season in series.seasons) {
            if (season.seasonNumber < prevSeason) {
                season.isWatched = !watchState
                seasonDao.update(season)

                val updateEpisodesSql = "UPDATE " + EpisodeEntry.TABLE_NAME +
                        " SET " + EpisodeEntry.COLUMN_EPISODE_WATCHED + " = " + (if (!watchState) 1 else 0).toString() +
                        " WHERE " + EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = " + season.id

                episodeDao.executeCustomSql(updateEpisodesSql)
            }

            if (season.seasonNumber == prevSeason) {
                for (episode in season.episodes) {
                    if (episode.episodeNumber < prevEpisode) {
                        episode.isWatched = !watchState
                        episodeDao.update(episode)
                    }
                }
            }
        }
    }

    fun delete(series: Series) {
        episodeDao.deleteBySeriesId(series.id)
        seasonDao.deleteBySeriesId(series.id)
        seriesDao.delete(series.id)
    }

    fun delete(seriesId: Long) {
        episodeDao.deleteBySeriesId(seriesId)
        seasonDao.deleteBySeriesId(seriesId)
        seriesDao.delete(seriesId)
    }

    fun episodeWatched(series: Series) {
        val seasons = ArrayList<Season>()
        for (season in series.seasons) {
            if (season.isWatched) {
                seasons.add(season)
            } else {
                val episodes = ArrayList<Episode>()
                for (episode in season.episodes) {
                    if (episode.isWatched) {
                        episodes.add(episode)
                    } else {
                        episode.isWatched = true
                        episodeDao.update(episode)
                        episodes.add(episode)
                        break
                    }
                }
                if (episodes.size == season.episodes.size) {
                    season.isWatched = true
                    seasonDao.update(season)
                    seasons.add(season)
                }
                break
            }
        }

        if (seasons.size == series.seasons.size && !series.isInProduction) {
            series.isWatched = true
            seriesDao.update(series, seriesDao.getHighestListPosition(true))
        }
    }

    fun episodeClicked(episode: Episode) {
        episode.isWatched = !episode.isWatched
        episodeDao.update(episode)

        if (!episode.isWatched) {
            val series = getSeriesById(episode.seriesId)
            series?.let {
                series.isWatched = false
                seriesDao.update(series, seriesDao.getHighestListPosition(false))
                for (season in series.seasons) {
                    if (season.id == episode.seasonId) {
                        season.isWatched = false
                        seasonDao.update(season)
                        break
                    }
                }
            }
        }
    }

    fun importSeries(series: Series) {
        val oldSeries = getSeriesById(series.id)
        if (oldSeries == null) {
            seriesDao.create(series)
            for (season in series.seasons) {
                seasonDao.create(season, series.id)
                for (episode in season.episodes) {
                    episodeDao.create(episode, series.id, season.id)
                }
            }
        } else {
            seriesDao.update(series, series.listPosition)
            for (season in series.seasons) {
                seasonDao.update(season)
                for (episode in season.episodes) {
                    episodeDao.update(episode)
                }
            }
        }
    }

    fun updateWatchState(series: Series) {
        val newListPosition = seriesDao.getHighestListPosition(series.isWatched)
        series.listPosition = newListPosition

        seriesDao.update(series, newListPosition)
    }

    fun update(series: Series) {
        getSeriesById(series.id)?.let { oldSeries ->
            val newPosition = getNewPosition(series, oldSeries)
            series.isWatched = oldSeries.isWatched
            series.listPosition = newPosition

            seriesDao.update(series, newPosition)

            for (season in series.seasons) {
                update(season, series)
            }
        }
    }

    fun updatePosition(series: Series) {
        getSeriesById(series.id)?.let { oldSeries ->
            val newPosition = getNewPosition(series, oldSeries)
            seriesDao.update(series, newPosition)
        }
    }

    private fun update(season: Season, series: Series) {
        val selection = SeasonEntry.ID + " = ?"
        val selectionArgs = arrayOf(java.lang.Long.toString(season.id))

        season.seriesId = series.id

        val seasons = seasonDao.read(selection, selectionArgs)
        if (seasons.isNotEmpty()) {
            val oldSeason = seasonDao.read(selection, selectionArgs)[0]
            season.isWatched = oldSeason.isWatched
            seasonDao.update(season)
        } else {
            season.isWatched = false
            seasonDao.create(season, series.id)

            series.isWatched = false
            seriesDao.update(series, series.listPosition)
        }

        for (episode in season.episodes) {
            update(episode, series, season)
        }
    }

    private fun update(episode: Episode, series: Series, season: Season) {
        val selection = EpisodeEntry.ID + " = ?"
        val selectionArgs = arrayOf(java.lang.Long.toString(episode.id))

        episode.seriesId = series.id
        episode.seasonId = season.id

        val episodes = episodeDao.read(selection, selectionArgs)

        if (episodes.isNotEmpty()) {
            val oldEpisode = episodes[0]
            episode.isWatched = oldEpisode.isWatched
            episodeDao.update(episode)
        } else {
            episode.isWatched = false
            episodeDao.create(episode)

            season.isWatched = false
            seasonDao.update(season)

            series.isWatched = false
            seriesDao.update(series, series.listPosition)
        }
    }

    private fun getNewPosition(updatedSeries: Series, oldSeries: Series): Int {
        return if (updatedSeries.isWatched == oldSeries.isWatched) {
            updatedSeries.listPosition
        } else seriesDao.getHighestListPosition(updatedSeries.isWatched)
    }

    private fun getSelectionArgs(state: WatchState): String {
        return if (state == WatchState.WATCH_STATE) "0" else "1"
    }

    companion object {

        private var instance: SeriesDbHelper? = null

        fun getInstance(context: Context): SeriesDbHelper {
            if (instance == null) {
                instance = SeriesDbHelper(context)
            }
            return instance!!
        }
    }
}
