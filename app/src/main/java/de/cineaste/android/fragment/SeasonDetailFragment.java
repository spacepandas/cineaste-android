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
import android.widget.ImageButton;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.EpisodeAdapter;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.viewholder.series.EpisodeViewHolder;

public class SeasonDetailFragment extends Fragment implements EpisodeViewHolder.OnEpisodeWatchStateChangeListener, EpisodeViewHolder.OnDescriptionShowToggleListener {

    private long seriesId;
    private long seasonId;
    private SeriesDbHelper seriesDbHelper;

    @Override
    public void watchStateChanged(Episode episode) {
        seriesDbHelper.episodeClicked(episode);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        seasonId = args.getLong("seasonId", -1);
        seriesId = args.getLong("seriesId", -1);

        seriesDbHelper = SeriesDbHelper.getInstance(getContext());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_series_detail, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.episodeRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);

        EpisodeAdapter adapter = new EpisodeAdapter(seriesDbHelper.getEpisodesBySeasonId(seasonId), this, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void showDescription(ImageButton showDescription, ImageButton hideDescription, TextView description) {
        showDescription.setVisibility(View.INVISIBLE);
        hideDescription.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDescription(ImageButton showDescription, ImageButton hideDescription, TextView description) {
        showDescription.setVisibility(View.VISIBLE);
        hideDescription.setVisibility(View.INVISIBLE);
        description.setVisibility(View.GONE);
    }
}
