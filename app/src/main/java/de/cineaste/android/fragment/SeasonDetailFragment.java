package de.cineaste.android.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.EpisodeAdapter;
import de.cineaste.android.database.dbHelper.EpisodeDbHelper;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.entity.series.Season;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;
import de.cineaste.android.util.DateAwareGson;
import de.cineaste.android.viewholder.series.EpisodeViewHolder;

public class SeasonDetailFragment extends Fragment implements EpisodeViewHolder.OnEpisodeWatchStateChangeListener {

    private int seasonNr;
    private long seriesId;
    private long seasonId;
    private EpisodeDbHelper db;
    private SeriesDbHelper seriesDbHelper;
    private RecyclerView recyclerView;
    private EpisodeAdapter adapter;
    private Runnable updateCallback;

    @Override
    public void watchStateChanged(Episode episode) {
        episode.setWatched(!episode.isWatched());
        db.updateWatchState(episode);
        Series currentSeries = seriesDbHelper.readSeries(seriesId);
        if (!episode.isWatched()) {
            currentSeries.setWatched(false);
        }

        Episode firstUnwatched = seriesDbHelper.findFirstUnWatchedEpisode(currentSeries);
        if (firstUnwatched != null) {
            Season currentSeason = seriesDbHelper.getSeasonFromId(currentSeries, firstUnwatched.getSeasonId());
            if (currentSeason != null) {
                currentSeries.setCurrentNumberOfSeason(currentSeason.getSeasonNumber());
                currentSeries.setCurrentNumberOfEpisode(firstUnwatched.getEpisodeNumber());
            }
        }

        seriesDbHelper.createOrUpdate(currentSeries);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        seasonNr = args.getInt("seasonNr", -1);
        seasonId = args.getLong("seasonId", -1);
        seriesId = args.getLong("seriesId", -1);

        db = EpisodeDbHelper.getInstance(getContext());
        seriesDbHelper = SeriesDbHelper.getInstance(getContext());

        updateCallback = getUpdateCallback();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_series_detail, container, false);

        recyclerView = view.findViewById(R.id.episodeRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        adapter = new EpisodeAdapter(db.readAllEpisodesOfSeason(seasonId), this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        autoUpdate();

        return view;
    }

    private void autoUpdate() {
        recyclerView.removeCallbacks(updateCallback);
        recyclerView.postDelayed(updateCallback, 1000);
    }

    private Runnable getUpdateCallback() {
        return new Runnable() {
            @Override
            public void run() {
                if (SeasonDetailFragment.this.isAdded()) {
                    loadSeason();
                }

            }
        };
    }

    private void loadSeason() {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getSeason(seriesId, seasonNr));
        client.sendRequest(new NetworkCallback() {
            @Override
            public void onFailure() {
            }

            @Override
            public void onSuccess(NetworkResponse response) {
                Gson gson = new DateAwareGson().getGson();
                JsonParser parser = new JsonParser();
                JsonObject responseObject =
                        parser.parse(response.getResponseReader()).getAsJsonObject();
                String episodesListJson = responseObject.get("episodes").toString();
                Type listType = new TypeToken<List<Episode>>(){}.getType();
                final List<Episode> episodes = gson.fromJson(episodesListJson, listType);

                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (Episode episode : episodes) {
                            episode.setSeasonId(seasonId);
                            db.createOrUpdate(episode);
                        }
                        adapter.update(episodes);
                        if (SeasonDetailFragment.this.isAdded()) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
    }

}
