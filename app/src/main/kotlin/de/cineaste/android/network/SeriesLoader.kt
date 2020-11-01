package de.cineaste.android.network

import android.content.Context
import android.content.res.Resources
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import de.cineaste.android.entity.series.Episode
import de.cineaste.android.entity.series.Season
import de.cineaste.android.entity.series.Series
import de.cineaste.android.util.DateAwareGson
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SeriesLoader(context: Context) {

    private val gson: Gson = DateAwareGson.gson
    private val resources: Resources = context.resources

    fun loadCompleteSeries(seriesId: Long, callback: SeriesCallback) {
        val client = NetworkClient()

        client.addRequest(getSeriesRequest(seriesId), object : NetworkCallback {
            override fun onFailure() {
                callback.onFailure()
            }

            override fun onSuccess(response: NetworkResponse) {
                val series = gson.fromJson(response.responseReader, Series::class.java)
                excludeSpecialsSeason(series)
                val responseCounter = CountDownLatch(series.seasons.size)

                for (season in series.seasons) {
                    loadEpisodesOfSeason(responseCounter, season, client, seriesId, callback)
                }

                try {
                    responseCounter.await(10L, TimeUnit.SECONDS)
                    callback.onSuccess(series)
                } catch (ex: InterruptedException) {
                    callback.onFailure()
                }
            }
        })
    }

    private fun loadEpisodesOfSeason(
        responseCounter: CountDownLatch,
        season: Season,
        client: NetworkClient,
        seriesId: Long,
        callback: SeriesCallback
    ) {
        client.addRequest(
            getSeasonRequest(seriesId, season.seasonNumber),
            object : NetworkCallback {
                override fun onFailure() {
                    callback.onFailure()
                }

                override fun onSuccess(response: NetworkResponse) {
                    responseCounter.countDown()
                    val episodes = parseResponse(response)
                    for (episode in episodes) {
                        episode.seasonId = season.id
                    }
                    season.episodes = episodes
                }
            })
    }

    private fun parseResponse(response: NetworkResponse): List<Episode> {
        val responseObject = JsonParser.parseReader(response.responseReader).asJsonObject
        val episodesListJson = responseObject.get("episodes").toString()
        val listType = object : TypeToken<List<Episode>>() {
        }.type
        return try {
            gson.fromJson<List<Episode>>(episodesListJson, listType)
        } catch (ex: Exception) {
            listOf()
        }
    }

    private fun excludeSpecialsSeason(series: Series) {
        val seasons = ArrayList<Season>()
        for (season in series.seasons) {
            if (season.seasonNumber != 0) {
                seasons.add(season)
            }
        }

        series.seasons = seasons
    }

    private fun getSeriesRequest(seriesId: Long): NetworkRequest {
        return NetworkRequest(resources).getSeries(seriesId)
    }

    private fun getSeasonRequest(seriesId: Long, seasonNumber: Int): NetworkRequest {
        return NetworkRequest(resources).getSeason(seriesId, seasonNumber)
    }
}
