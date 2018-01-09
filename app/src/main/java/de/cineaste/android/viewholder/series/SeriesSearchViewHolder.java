package de.cineaste.android.viewholder.series;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesSearchQueryAdapter;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.listener.ItemClickListener;

public class SeriesSearchViewHolder extends AbstractSeriesViewHolder {

    private final TextView releaseDate;
    private final Button addToWatchlistButton;
    private final Button watchedButton;
    private final SeriesSearchQueryAdapter.OnSeriesStateChange seriesStateChange;

    public SeriesSearchViewHolder(View itemView, ItemClickListener listener, Context context, SeriesSearchQueryAdapter.OnSeriesStateChange seriesStateChange) {
        super(itemView, listener, context);
        this.seriesStateChange = seriesStateChange;

        this.releaseDate = itemView.findViewById(R.id.releaseDate);
        this.addToWatchlistButton = itemView.findViewById(R.id.to_watchlist_button);
        this.watchedButton = itemView.findViewById(R.id.history_button);
    }

    @Override
    public void assignData(final Series series, int position) {
        setBaseInformation(series);
        if (series.getReleaseDate() != null) {
            releaseDate.setText(convertDate(series.getReleaseDate()));
            releaseDate.setVisibility(View.VISIBLE);
        } else {
            releaseDate.setVisibility(View.GONE);
        }

        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = SeriesSearchViewHolder.this.getAdapterPosition();
                seriesStateChange.onSeriesStateChangeListener(series, v.getId(), index);
            }
        });

        watchedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = SeriesSearchViewHolder.this.getAdapterPosition();
                seriesStateChange.onSeriesStateChangeListener(series, v.getId(), index);
            }
        });
    }
}
