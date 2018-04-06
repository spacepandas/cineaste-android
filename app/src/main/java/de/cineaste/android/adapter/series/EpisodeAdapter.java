package de.cineaste.android.adapter.series;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.viewholder.series.EpisodeViewHolder;


public class EpisodeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Episode> episodes = new ArrayList<>();
    private final EpisodeViewHolder.OnEpisodeWatchStateChangeListener onEpisodeWatchStateChangeListener;
    private final EpisodeViewHolder.OnDescriptionShowToggleListener onDescriptionShowToggleListener;

    public EpisodeAdapter(List<Episode> episodes, EpisodeViewHolder.OnEpisodeWatchStateChangeListener onEpisodeWatchStateChangeListener, EpisodeViewHolder.OnDescriptionShowToggleListener onDescriptionShowToggleListener) {
        this.episodes.clear();
        this.episodes.addAll(episodes);
        this.onEpisodeWatchStateChangeListener = onEpisodeWatchStateChangeListener;
        this.onDescriptionShowToggleListener = onDescriptionShowToggleListener;
    }

    @Override
    public EpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_episode, parent, false);

        return new EpisodeViewHolder(view, onEpisodeWatchStateChangeListener, onDescriptionShowToggleListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EpisodeViewHolder) holder).assignData(episodes.get(position));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void update(List<Episode> episodes) {
        this.episodes.clear();
        this.episodes.addAll(episodes);
    }
}
