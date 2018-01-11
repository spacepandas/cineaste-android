package de.cineaste.android.viewholder.movie;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.listener.ItemClickListener;

public class MovieViewHolder extends AbstractMovieViewHolder {

    private final TextView movieVote;

    public MovieViewHolder(View itemView, Context context, ItemClickListener listener) {
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
                    listener.onItemClickListener(movie.getId(),
                            new View[]{view, poster, title, movieRuntime, movieVote});
            }
        });
    }

    public void onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY);
    }

    public void onItemClear() {
        itemView.setBackgroundColor(Color.WHITE);
    }
}
