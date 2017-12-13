package de.cineaste.android.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.entity.Series;
import de.cineaste.android.listener.ItemClickListener;

public class SeriesViewHolder extends AbstractSeriesViewHolder {

    private TextView seasons;
    private TextView currentStatus;

    public SeriesViewHolder(View itemView, ItemClickListener listener, Context context) {
        super(itemView, listener, context);

        seasons = itemView.findViewById(R.id.season);
        currentStatus = itemView.findViewById(R.id.current);
    }

    @Override
    public void assignData(final Series series) {
        setBaseInformation(series);

        seasons.setText(resources.getString(R.string.seasons, String.valueOf(series.getNumberOfSeasons())));
        currentStatus.setText(resources.getString(R.string.currentStatus,
                String.valueOf(series.getCurrentNumberOfSeason()),
                String.valueOf(series.getCurrentNumberOfEpisode())));
    }
}
