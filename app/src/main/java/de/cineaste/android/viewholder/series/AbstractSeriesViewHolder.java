package de.cineaste.android.viewholder.series;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cineaste.android.R;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.util.Constants;

public abstract class AbstractSeriesViewHolder extends RecyclerView.ViewHolder {

    final TextView title;
    final TextView vote;
    final ImageView poster;
    final ItemClickListener listener;
    final View view;
    final Resources resources;
    final Context context;


    public AbstractSeriesViewHolder(View itemView, ItemClickListener listener, Context context) {
        super(itemView);
        this.context = context;
        this.listener = listener;
        this.resources = context.getResources();

        this.title = itemView.findViewById(R.id.title);
        this.vote = itemView.findViewById(R.id.vote);
        this.poster = itemView.findViewById(R.id.poster_image_view);
        this.view = itemView;
    }

    public abstract void assignData(final Series series, final int position);

    void setBaseInformation(Series series) {
        title.setText(series.getName());
        vote.setText(resources.getString(R.string.vote, String.valueOf(series.getVoteAverage())));
        setMoviePoster(series);
    }

    private void setMoviePoster(Series series) {
        String posterName = series.getPosterPath();
        if (TextUtils.isEmpty(posterName)) {
            posterName = series.getPosterPath();
        }
        String posterUri =
                Constants.POSTER_URI_SMALL
                        .replace("<posterName>", posterName != null ? posterName : "/")
                        .replace("<API_KEY>", context.getString(R.string.movieKey));
        Picasso.with(context).load(posterUri).resize(222, 334).error(R.drawable.placeholder_poster).into(poster);
    }

    protected String convertDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", resources.getConfiguration().locale);
        return simpleDateFormat.format(date);
    }
}
