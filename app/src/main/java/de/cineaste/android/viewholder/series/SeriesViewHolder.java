package de.cineaste.android.viewholder.series;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesListAdapter;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.fragment.WatchState;
import de.cineaste.android.listener.ItemClickListener;

public class SeriesViewHolder extends AbstractSeriesViewHolder {

    private TextView seasons;
    private TextView currentStatus;
    private ImageButton episodeSeen;
    private SeriesListAdapter.OnEpisodeWatchedClickListener onEpisodeWatchedClickListener;

    public SeriesViewHolder(View itemView, ItemClickListener listener, Context context, WatchState state, SeriesListAdapter.OnEpisodeWatchedClickListener onEpisodeWatchedClickListener) {
        super(itemView, listener, context);

        this.onEpisodeWatchedClickListener = onEpisodeWatchedClickListener;
        seasons = itemView.findViewById(R.id.season);
        currentStatus = itemView.findViewById(R.id.current);
        episodeSeen = itemView.findViewById(R.id.episode_seen);

        if (state == WatchState.WATCHED_STATE) {
            episodeSeen.setVisibility(View.GONE);
        }
    }

    @Override
    public void assignData(final Series series, final int position) {
        setBaseInformation(series);

        seasons.setText(resources.getString(R.string.seasons, String.valueOf(series.getNumberOfSeasons())));
        currentStatus.setText(resources.getString(R.string.currentStatus,
                String.valueOf(series.getCurrentNumberOfSeason()),
                String.valueOf(series.getCurrentNumberOfEpisode())));

        episodeSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEpisodeWatchedClickListener.onEpisodeWatchedClick(series, position);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClickListener(series.getId(), new View[]{view, poster, title});
                }
            }
        });
    }

    public void onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY);
    }

    public void onItemClear() {
        itemView.setBackgroundColor(Color.WHITE);
    }
}
