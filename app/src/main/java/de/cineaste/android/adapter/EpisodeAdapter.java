package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.database.EpisodeDbHelper;
import de.cineaste.android.entity.Episode;
import de.cineaste.android.viewholder.EpisodeViewHolder;


public class EpisodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final long seasonId;
    private final Context context;
    private final EpisodeDbHelper db;
    private List<Episode> episodes = new ArrayList<>();

    public EpisodeAdapter(long seasonId, Context context) {
        this.seasonId = seasonId;
        this.context = context;
        this.db = EpisodeDbHelper.getInstance(context);
        this.episodes.clear();
        this.episodes.addAll(db.readAllEpisodesOfSeason(seasonId));
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_episode, parent, false);

        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EpisodeViewHolder) holder).assignData(episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void update() {
        this.episodes.clear();
        this.episodes.addAll(db.readAllEpisodesOfSeason(seasonId));
    }
}
