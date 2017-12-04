package de.cineaste.android.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cineaste.android.util.Constants;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.listener.MovieClickListener;

abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    final TextView movieRuntime;
    final MovieClickListener listener;
    final TextView movieTitle;
    final ImageView moviePoster;
    final View view;
    final Resources resources;
    private final Context context;
    private final TextView movieReleaseDate;

    AbstractViewHolder(View itemView, Context context, MovieClickListener listener) {
        super(itemView);
        this.context = context;
        this.listener = listener;
        this.resources = context.getResources();
        movieTitle = itemView.findViewById(R.id.movie_title);
        movieReleaseDate = itemView.findViewById(R.id.movieReleaseDate);
        movieRuntime = itemView.findViewById(R.id.movieRuntime);
        moviePoster = itemView.findViewById(R.id.movie_poster_image_view);
        view = itemView;
    }

    public abstract void assignData(final Movie movie);

    void setBaseInformation(Movie movie) {
        movieTitle.setText(movie.getTitle());
        if (movie.getReleaseDate() != null) {
            movieReleaseDate.setText(convertDate(movie.getReleaseDate()));
            movieReleaseDate.setVisibility(View.VISIBLE);
        } else {
            movieReleaseDate.setVisibility(View.GONE);
        }
        setMoviePoster(movie);
    }

    private void setMoviePoster(Movie movie) {
        String posterName = movie.getPosterPath();
        String posterUri =
                Constants.POSTER_URI_SMALL
                        .replace("<posterName>", posterName != null ? posterName : "/")
                .replace("<API_KEY>", context.getString(R.string.movieKey));
        Picasso.with(context).load(posterUri).resize(222, 334).error(R.drawable.placeholder_poster).into(moviePoster);
    }

    private String convertDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", resources.getConfiguration().locale);
        return simpleDateFormat.format(date);
    }
}
