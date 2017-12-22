package de.cineaste.android.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.entity.Episode;

public class EpisodeViewHolder extends RecyclerView.ViewHolder {

    private TextView episodeNumber;
    private TextView episodeTitle;
    private CheckBox checkBox;
    private OnEpisodeWatchStateChangeListener onEpisodeWatchStateChangeListener;

    public interface OnEpisodeWatchStateChangeListener {
        void watchStateChanged(Episode episode);
    }

    public EpisodeViewHolder(View itemView, OnEpisodeWatchStateChangeListener onEpisodeWatchStateChangeListener) {
        super(itemView);

        this.onEpisodeWatchStateChangeListener = onEpisodeWatchStateChangeListener;
        episodeNumber = itemView.findViewById(R.id.episodeNumber);
        episodeTitle = itemView.findViewById(R.id.episodeTitle);
        checkBox = itemView.findViewById(R.id.checkBox);
    }

    public void assignData(final Episode episode) {
        episodeNumber.setText(String.valueOf(episode.getEpisodeNumber()));
        episodeTitle.setText(episode.getName());
        checkBox.setChecked(episode.isWatched());

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEpisodeWatchStateChangeListener.watchStateChanged(episode);
            }
        });
    }
}
