package de.cineaste.android.viewholder.series;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cineaste.android.R;
import de.cineaste.android.entity.series.Season;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.util.Constants;


public class SeasonViewHolder extends RecyclerView.ViewHolder {

    private ImageView poster;
    private TextView seasonNumber;
    private TextView numberOfEpisodes;
    private TextView releaseDate;
    private Context context;
    private Resources resources;
    private ItemClickListener itemClickListener;
    private View view;

    public SeasonViewHolder(View itemView, ItemClickListener listener, Context context) {
        super(itemView);
        poster = itemView.findViewById(R.id.poster_image_view);
        seasonNumber = itemView.findViewById(R.id.season);
        numberOfEpisodes = itemView.findViewById(R.id.episodes);
        releaseDate = itemView.findViewById(R.id.releaseDate);
        this.resources = context.getResources();
        view = itemView;
        itemClickListener = listener;
        this.context = context;
    }

    public void assignData(final Season season) {
        seasonNumber.setText(resources.getString(R.string.currentSeason, String.valueOf(season.getSeasonNumber())));
        numberOfEpisodes.setText(resources.getString(R.string.episodes, String.valueOf(season.getEpisodeCount())));
        if (season.getReleaseDate() == null) {
            releaseDate.setVisibility(View.GONE);
        } else {
            releaseDate.setText(convertDate(season.getReleaseDate()));
        }

        setMoviePoster(season);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClickListener(season.getId(), new View[] {view, poster});
                }
            }
        });

    }

    private void setMoviePoster(Season season) {
        String posterName = season.getPosterPath();
        String posterUri =
                Constants.Companion.getPOSTER_URI_SMALL()
                        .replace("<posterName>", posterName != null ? posterName : "/")
                        .replace("<API_KEY>", context.getString(R.string.movieKey));
        Picasso.with(context).load(posterUri).resize(342, 513).error(R.drawable.placeholder_poster).into(poster);
    }

    private String convertDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", resources.getConfiguration().locale);
        return simpleDateFormat.format(date);
    }
}
