package de.cineaste.android.viewholder.movie;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.viewholder.BaseViewHolder;

abstract class AbstractMovieViewHolder extends BaseViewHolder {

    private final TextView movieReleaseDate;
    final TextView movieRuntime;

    AbstractMovieViewHolder(View itemView, Context context, ItemClickListener listener) {
        super(itemView, listener, context);
        movieReleaseDate = itemView.findViewById(R.id.movieReleaseDate);
        movieRuntime = itemView.findViewById(R.id.movieRuntime);
    }

    @SuppressWarnings("unused")
    public abstract void assignData(final Movie movie);

    void setBaseInformation(Movie movie) {
        title.setText(movie.getTitle());
        if (movie.getReleaseDate() != null) {
            movieReleaseDate.setText(convertDate(movie.getReleaseDate()));
            movieReleaseDate.setVisibility(View.VISIBLE);
        } else {
            movieReleaseDate.setVisibility(View.GONE);
        }
        setPoster(movie.getPosterPath());
    }
}
