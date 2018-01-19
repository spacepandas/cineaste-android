package de.cineaste.android.network;


import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.entity.series.Season;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.util.DateAwareGson;

public class SeriesLoader {

    private final Gson gson;
    private final Resources resources;

    public SeriesLoader(Context context) {
        this.gson = new DateAwareGson().getGson();
        this.resources = context.getResources();
    }

    public void loadCompleteSeries(final long seriesId, final SeriesCallback callback) {
        final NetworkClient client = new NetworkClient();

        client.addRequest(getSeriesRequest(seriesId), new NetworkCallback() {
            @Override
            public void onFailure() {
                callback.onFailure();
            }

            @Override
            public void onSuccess(NetworkResponse response) {
                final Series series = gson.fromJson(response.getResponseReader(), Series.class);
                excludeSpecialsSeason(series);
                final CountDownLatch responseCounter = new CountDownLatch(series.getSeasons().size());

                for (final Season season : series.getSeasons()) {
                    loadEpisodesOfSeason(responseCounter, season, client, seriesId, callback);
                }

                try {
                    responseCounter.await(10L, TimeUnit.SECONDS);
                    callback.onSuccess(series);
                } catch (InterruptedException ex) {
                    callback.onFailure();
                }
            }
        });
    }

    private void loadEpisodesOfSeason(final CountDownLatch responseCounter, final Season season, NetworkClient client, long seriesId, final SeriesCallback callback) {
        client.addRequest(getSeasonRequest(seriesId, season.getSeasonNumber()), new NetworkCallback() {
            @Override
            public void onFailure() {
                callback.onFailure();
            }

            @Override
            public void onSuccess(NetworkResponse response) {
                responseCounter.countDown();
                final List<Episode> episodes = parseResponse(response);
                for (Episode episode : episodes) {
                    episode.setSeasonId(season.getId());
                }
                season.setEpisodes(episodes);
            }
        });
    }

    private List<Episode> parseResponse(NetworkResponse response) {
        JsonParser parser = new JsonParser();
        JsonObject responseObject =
                parser.parse(response.getResponseReader()).getAsJsonObject();
        String episodesListJson = responseObject.get("episodes").toString();
        Type listType = new TypeToken<List<Episode>>(){}.getType();
        try {
            return gson.fromJson(episodesListJson, listType);
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    private void excludeSpecialsSeason(Series series) {
        List<Season> seasons = new ArrayList<>();
        for (Season season : series.getSeasons()) {
            if (season.getSeasonNumber() != 0) {
                seasons.add(season);
            }
        }

        series.getSeasons().clear();
        series.setSeasons(seasons);
    }

    private NetworkRequest getSeriesRequest(long seriesId) {
        return new NetworkRequest(resources).getSeries(seriesId);
    }

    private NetworkRequest getSeasonRequest(long seriesId, int seasonNumber) {
        return new NetworkRequest(resources).getSeason(seriesId, seasonNumber);
    }
}
