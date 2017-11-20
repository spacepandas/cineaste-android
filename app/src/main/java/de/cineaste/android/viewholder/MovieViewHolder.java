package de.cineaste.android.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.listener.MovieClickListener;

public class MovieViewHolder extends AbstractViewHolder {

    private final TextView movieVote;

    public MovieViewHolder(View itemView, Context context, MovieClickListener listener) {
        super(itemView, context, listener);

        movieVote = itemView.findViewById(R.id.movie_vote);
    }

    @Override
    public void assignData(final Movie movie) {
        setBaseInformation(movie);

        movieVote.setText(resources.getString(R.string.vote, String.valueOf(movie.getVoteAverage())));

        movieRuntime.setText(resources.getString(R.string.runtime, movie.getRuntime()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onMovieClickListener(movie.getId(),
                            new View[]{view, moviePoster, movieTitle, movieRuntime, movieVote});
            }
        });
    }
}
