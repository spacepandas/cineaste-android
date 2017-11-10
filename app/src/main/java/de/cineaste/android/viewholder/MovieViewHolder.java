package de.cineaste.android.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.listener.MovieClickListener;

public class MovieViewHolder extends AbstractViewHolder {

    public MovieViewHolder(View itemView, Context context, MovieClickListener listener) {
        super(itemView, context, listener);
        LinearLayout linearLayout = itemView.findViewById(R.id.buttons);
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    public void assignData(final Movie movie) {
        setBaseInformation(movie);

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
