package de.cineaste.android.database.dbHelper

import android.content.Context
import de.cineaste.android.db
import de.cineaste.android.entity.series.*
import de.cineaste.android.fragment.WatchState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SeriesDbHelper private constructor(context: Context) {

    private val seriesDao = db!!.seriesDao()
    private val seasonDao = db!!.seasonDao()
    private val episodeDao = db!!.episodeDao()

    val allSeries: List<Series>
        get() {
            val seriesEntityList = mutableListOf<SeriesEntity>()
            GlobalScope.launch { seriesEntityList.addAll(seriesDao.getAll()) }
            val seriesList = mutableListOf<Series>()
            for (series in seriesEntityList) {
                val seriesModel = toSeriesModel(series)
                seriesModel?.let {
                    seriesList.add(seriesModel)
                }
            }
            return seriesList
        }

    private fun toSeriesModel(seriesEntity: SeriesEntity?): Series? {
        val seasonEntities = mutableListOf<SeasonEntity>()
        GlobalScope.launch {
            seriesEntity?.let {
                seasonEntities.addAll(seasonDao.getBySeriesId(seriesEntity.id))
            }
        }
        val season = mutableListOf<Season>()
        for (seasonEntity in seasonEntities) {
            val episodeEntities = mutableListOf<EpisodeEntity>()
            GlobalScope.launch { episodeEntities.addAll(episodeDao.getBySeasonId(seasonEntity.id)) }
            season.add(seasonEntity.toModel(episodeEntities.map { it.toModel() }))
        }

        return seriesEntity?.toModel(season)
    }

    fun getSeriesById(seriesId: Long): Series? {
        var series: SeriesEntity? = null
        GlobalScope.launch { series = seriesDao.getOne(seriesId) }
        return toSeriesModel(series)
    }

    private fun getStatusFromState(state: WatchState): Boolean {
        return when (state) {
            WatchState.WATCH_STATE -> false
            WatchState.WATCHED_STATE -> true
            else -> false
        }
    }

    fun getSeriesByWatchedState(watchedState: WatchState): List<Series> {
        val seriesEntityList = mutableListOf<SeriesEntity>()
        GlobalScope.launch {
            seriesEntityList.addAll(
                seriesDao.getAllByWatchState(
                    getStatusFromState(
                        watchedState
                    )
                )
            )
        }
        val seriesList = mutableListOf<Series>()
        for (series in seriesEntityList) {
            val seriesModel = toSeriesModel(series)
            seriesModel?.let {
                seriesList.add(seriesModel)
            }
        }

        return seriesList
    }

    fun getUnWatchedEpisodesOfSeries(seriesId: Long): List<Episode> {
        val episodeEntityList = mutableListOf<EpisodeEntity>()
        GlobalScope.launch {
            episodeEntityList.addAll(
                episodeDao.getBySeriesAndWatchedState(
                    seriesId,
                    false
                )
            )
        }
        return episodeEntityList.map { it.toModel() }
    }

    fun getEpisodesBySeasonId(seasonId: Long): List<Episode> {
        val episodeEntityList = mutableListOf<EpisodeEntity>()
        GlobalScope.launch {
            episodeEntityList.addAll(episodeDao.getBySeasonId(seasonId))
        }
        return episodeEntityList.map { it.toModel() }
    }

    fun reorderAlphabetical(state: WatchState): List<Series> {
        return getSeriesByWatchedState(state).sortedBy { it.name }
    }

    fun reorderByReleaseDate(state: WatchState): List<Series> {
        return getSeriesByWatchedState(state).sortedBy { it.releaseDate }
    }

    private fun getSeasonById(seasonId: Long): Season? {
        var seasonEntity: SeasonEntity? = null
        val episodeEntitiesOfSeason = mutableListOf<EpisodeEntity>()

        GlobalScope.launch {
            seasonEntity = seasonDao.getOne(seasonId)
            episodeEntitiesOfSeason.addAll(episodeDao.getBySeasonId(seasonId))
        }

        return seasonEntity?.toModel(episodeEntitiesOfSeason.map { it.toModel() })
    }


    fun addToWatchList(series: Series) {
        addToList(series, false)
    }

    fun addToHistory(series: Series) {
        addToList(series, true)
    }

    private fun addToList(series: Series, watchState: Boolean) {
        GlobalScope.launch {
            series.isWatched = watchState
            series.listPosition = seriesDao.getHighestListPosition(watchState)

            seriesDao.insert(series.toEntity())
            for (season in series.seasons) {
                season.isWatched = watchState
                season.seriesId = series.id
                seasonDao.insert(season.toEntity())
                for (episode in season.episodes) {
                    episode.isWatched = watchState
                    episode.seasonId = season.id
                    episode.seriesId = series.id
                    episodeDao.insert(episode.toEntity())
                }
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
        GlobalScope.launch {
            getSeasonById(seasonId)?.let { season ->
                val watchState = !season.isWatched
                seasonDao.updateWatchedStateBySeasonId(watchState, seasonId)
                episodeDao.updateWatchedStateBySeasonId(watchState, seasonId)
            }
        }
    }

    private fun moveBetweenLists(series: Series, watchState: Boolean) {
        GlobalScope.launch {
            series.isWatched = watchState
            series.listPosition = seriesDao.getHighestListPosition(watchState)

            seriesDao.update(series.toEntity())

            seasonDao.updateWatchedStateBySeriesId(watchState, series.id)
            episodeDao.updateWatchedStateBySeriesId(watchState, series.id)
        }
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
        GlobalScope.launch {
            moveBetweenLists(series, watchState)
            for (season in series.seasons) {
                if (season.seasonNumber < prevSeason) {
                    season.isWatched = !watchState
                    seasonDao.update(season.toEntity())

                    episodeDao.updateWatchedStateBySeasonId(season.isWatched, season.id)
                }

                if (season.seasonNumber == prevSeason) {
                    for (episode in season.episodes) {
                        if (episode.episodeNumber < prevEpisode) {
                            episode.isWatched = !watchState
                            episodeDao.update(episode.toEntity())
                        }
                    }
                }
            }
        }
    }

    fun delete(series: Series) {
        GlobalScope.launch {
            episodeDao.deleteBySeriesId(series.id)
            seasonDao.deleteBySeriesId(series.id)
            seriesDao.delete(series.toEntity())
        }
    }

    fun delete(seriesId: Long) {
        GlobalScope.launch {
            episodeDao.deleteBySeriesId(seriesId)
            seasonDao.deleteBySeriesId(seriesId)
            val series = getSeriesById(seriesId)?.toEntity()
            series?.let {
                seriesDao.delete(it)
            }
        }
    }

    fun episodeWatched(series: Series) {
        GlobalScope.launch {
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
                            episodeDao.update(episode.toEntity())
                            episodes.add(episode)
                            break
                        }
                    }
                    if (episodes.size == season.episodes.size) {
                        season.isWatched = true
                        seasonDao.update(season.toEntity())
                        seasons.add(season)
                    }
                    break
                }
            }

            if (seasons.size == series.seasons.size && !series.isInProduction) {
                series.isWatched = true
                series.listPosition = seriesDao.getHighestListPosition(true)
                seriesDao.update(series.toEntity())
            }
        }
    }

    fun episodeClicked(episode: Episode) {
        GlobalScope.launch {
            episode.isWatched = !episode.isWatched
            episodeDao.update(episode.toEntity())

            if (!episode.isWatched) {
                val series = getSeriesById(episode.seriesId)
                series?.let {
                    series.isWatched = false
                    series.listPosition = seriesDao.getHighestListPosition(false)
                    seriesDao.update(series.toEntity())
                    for (season in series.seasons) {
                        if (season.id == episode.seasonId) {
                            season.isWatched = false
                            seasonDao.update(season.toEntity())
                            break
                        }
                    }
                }
            }
        }
    }

    fun importSeries(series: Series) {
        GlobalScope.launch {
            val oldSeries = getSeriesById(series.id)
            if (oldSeries == null) {
                seriesDao.insert(series.toEntity())
                for (season in series.seasons) {
                    season.seriesId = series.id
                    seasonDao.insert(season.toEntity())
                    for (episode in season.episodes) {
                        episode.seriesId = series.id
                        episode.seasonId = season.id
                        episodeDao.insert(episode.toEntity())
                    }
                }
            } else {
                seriesDao.update(series.toEntity())
                for (season in series.seasons) {
                    seasonDao.update(season.toEntity())
                    for (episode in season.episodes) {
                        episodeDao.update(episode.toEntity())
                    }
                }
            }
        }
    }

    fun updateWatchState(series: Series) {
        GlobalScope.launch {
            val newListPosition = seriesDao.getHighestListPosition(series.isWatched)
            series.listPosition = newListPosition

            seriesDao.update(series.toEntity())
        }
    }

    fun update(series: Series) {
        GlobalScope.launch {
            getSeriesById(series.id)?.let { oldSeries ->
                val newPosition = getNewPosition(series, oldSeries)
                series.isWatched = oldSeries.isWatched
                series.listPosition = newPosition

                seriesDao.update(series.toEntity())

                for (season in series.seasons) {
                    update(season, series)
                }
            }
        }
    }

    fun updatePosition(series: Series) {
        GlobalScope.launch {
            getSeriesById(series.id)?.let { oldSeries ->
                series.listPosition = getNewPosition(series, oldSeries)
                seriesDao.update(series.toEntity())
            }
        }
    }

    private fun update(season: Season, series: Series) {
        GlobalScope.launch {
            season.seriesId = series.id

            val seasons = seasonDao.getOne(season.id)
            if (seasons != null) {
                season.isWatched = seasons.isWatched
                seasonDao.update(season.toEntity())
            } else {
                season.isWatched = false
                season.seriesId = series.id
                seasonDao.insert(season.toEntity())

                series.isWatched = false
                seriesDao.update(series.toEntity())
            }

            for (episode in season.episodes) {
                update(episode, series, season)
            }
        }
    }

    private fun update(episode: Episode, series: Series, season: Season) {
        GlobalScope.launch {
            episode.seriesId = series.id
            episode.seasonId = season.id

            val episodes = episodeDao.getOne(episode.id)

            if (episodes != null) {
                episode.isWatched = episodes.isWatched
                episodeDao.update(episode.toEntity())
            } else {
                episode.isWatched = false
                episodeDao.insert(episode.toEntity())

                season.isWatched = false
                seasonDao.update(season.toEntity())

                series.isWatched = false
                seriesDao.update(series.toEntity())
            }
        }
    }

    private fun getNewPosition(updatedSeries: Series, oldSeries: Series): Int {
        var positon = 0
        if (updatedSeries.isWatched == oldSeries.isWatched) {
            positon = updatedSeries.listPosition
        } else {
            GlobalScope.launch {
                positon = seriesDao.getHighestListPosition(updatedSeries.isWatched)
            }
        }
        return positon
    }

    companion object {
        @Volatile
        private var instance: SeriesDbHelper? = null

        fun getInstance(context: Context): SeriesDbHelper {
            return instance ?: synchronized(this) {
                val dbHelper = SeriesDbHelper(context)
                instance = dbHelper
                return dbHelper
            }
        }
    }
}
