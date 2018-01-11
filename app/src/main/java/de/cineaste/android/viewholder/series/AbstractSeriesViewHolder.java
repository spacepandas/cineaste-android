package de.cineaste.android.viewholder.series;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.viewholder.BaseViewHolder;

public abstract class AbstractSeriesViewHolder extends BaseViewHolder {

    private final TextView vote;

    AbstractSeriesViewHolder(View itemView, ItemClickListener listener, Context context) {
        super(itemView, listener, context);
        this.vote = itemView.findViewById(R.id.vote);
    }

    public abstract void assignData(final Series series, final int position);

    void setBaseInformation(Series series) {
        title.setText(series.getName());
        vote.setText(resources.getString(R.string.vote, String.valueOf(series.getVoteAverage())));


        String posterName = series.getPosterPath();
        if (TextUtils.isEmpty(posterName)) {
            posterName = series.getPosterPath();
        }
        setPoster(posterName);
    }
}
