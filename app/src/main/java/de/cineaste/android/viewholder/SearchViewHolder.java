package de.cineaste.android.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import de.cineaste.android.R;
import de.cineaste.android.adapter.SearchQueryAdapter;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.listener.MovieClickListener;

public class SearchViewHolder extends AbstractViewHolder {

    private final Button addToWatchlistButton;
    private final Button movieWatchedButton;
    private final SearchQueryAdapter.OnMovieStateChange movieStateChange;

    public SearchViewHolder(View itemView, Context context, SearchQueryAdapter.OnMovieStateChange movieStateChange, MovieClickListener listener) {
        super(itemView, context, listener);

        addToWatchlistButton = itemView.findViewById(R.id.to_watchlist_button);
        movieWatchedButton = itemView.findViewById(R.id.watched_button);
        this.movieStateChange = movieStateChange;

        movieRuntime.setVisibility(View.GONE);
    }

    @Override
    public void assignData(final Movie movie) {
        setBaseInformation(movie);

        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = SearchViewHolder.this.getAdapterPosition();
                movieStateChange.onMovieStateChangeListener(movie, v.getId(), index);
            }
        });

        movieWatchedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = SearchViewHolder.this.getAdapterPosition();
                movieStateChange.onMovieStateChangeListener(movie, v.getId(), index);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMovieClickListener(movie.getId(), new View[]{view, moviePoster});
            }
        });
    }
}
