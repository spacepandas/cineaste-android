package de.cineaste.android.viewholder.series;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.entity.series.Episode;

public class EpisodeViewHolder extends RecyclerView.ViewHolder {

    private TextView episodeNumber;
    private TextView episodeTitle;
    private TextView description;
    private ImageButton showDescription;
    private ImageButton hideDescription;
    private CheckBox checkBox;
    private OnEpisodeWatchStateChangeListener onEpisodeWatchStateChangeListener;
    private OnDescriptionShowToggleListener onDescriptionShowToggleListener;

    public interface OnEpisodeWatchStateChangeListener {
        void watchStateChanged(Episode episode);
    }

    public interface OnDescriptionShowToggleListener {
        void showDescription(ImageButton showDescription, ImageButton hideDescription, TextView description);
        void hideDescription(ImageButton showDescription, ImageButton hideDescription, TextView description);
    }

    public EpisodeViewHolder(View itemView, OnEpisodeWatchStateChangeListener onEpisodeWatchStateChangeListener, OnDescriptionShowToggleListener onDescriptionShowToggleListener) {
        super(itemView);

        this.onEpisodeWatchStateChangeListener = onEpisodeWatchStateChangeListener;
        this.onDescriptionShowToggleListener = onDescriptionShowToggleListener;
        episodeNumber = itemView.findViewById(R.id.episodeNumber);
        episodeTitle = itemView.findViewById(R.id.episodeTitle);
        description = itemView.findViewById(R.id.description);
        showDescription = itemView.findViewById(R.id.show_more);
        hideDescription = itemView.findViewById(R.id.show_less);
        checkBox = itemView.findViewById(R.id.checkBox);
    }

    public void assignData(final Episode episode) {
        episodeNumber.setText(String.valueOf(episode.getEpisodeNumber()));
        episodeTitle.setText(episode.getName());
        description.setText(episode.getDescription());
        checkBox.setChecked(episode.isWatched());

        showDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDescriptionShowToggleListener.showDescription(showDescription, hideDescription, description);
            }
        });

        hideDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.util.Log.d("mgr", "hide");
                onDescriptionShowToggleListener.hideDescription(showDescription, hideDescription, description);
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEpisodeWatchStateChangeListener.watchStateChanged(episode);
            }
        });
    }
}
