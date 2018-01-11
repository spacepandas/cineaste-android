package de.cineaste.android.viewholder.movie;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import de.cineaste.android.R;
import de.cineaste.android.adapter.movie.MovieSearchQueryAdapter;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.listener.ItemClickListener;

public class MovieSearchViewHolder extends AbstractMovieViewHolder {

    private final Button addToWatchlistButton;
    private final Button movieWatchedButton;
    private final MovieSearchQueryAdapter.OnMovieStateChange movieStateChange;

    public MovieSearchViewHolder(View itemView, Context context, MovieSearchQueryAdapter.OnMovieStateChange movieStateChange, ItemClickListener listener) {
        super(itemView, context, listener);

        addToWatchlistButton = itemView.findViewById(R.id.to_watchlist_button);
        movieWatchedButton = itemView.findViewById(R.id.history_button);
        this.movieStateChange = movieStateChange;

    }

    @Override
    public void assignData(final Movie movie) {
        setBaseInformation(movie);

        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = MovieSearchViewHolder.this.getAdapterPosition();
                movieStateChange.onMovieStateChangeListener(movie, v.getId(), index);
            }
        });

        movieWatchedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = MovieSearchViewHolder.this.getAdapterPosition();
                movieStateChange.onMovieStateChangeListener(movie, v.getId(), index);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(movie.getId(), new View[]{view, poster});
            }
        });
    }
}
