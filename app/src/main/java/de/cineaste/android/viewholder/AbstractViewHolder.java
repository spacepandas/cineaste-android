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

import de.cineaste.android.Constants;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.listener.MovieClickListener;

/**
 * Created by marcelgross on 10.11.17.
 */

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    protected final Context context;
    protected final MovieClickListener listener;
    protected final TextView movieTitle;
    protected final TextView movieReleaseDate;
    protected final TextView movieRuntime;
    protected final TextView movieVote;
    protected final ImageView moviePoster;
    protected final View view;
    protected final Resources resources;

    public AbstractViewHolder(View itemView, Context context, MovieClickListener listener) {
        super(itemView);
        this.context = context;
        this.listener = listener;
        this.resources = context.getResources();
        movieTitle = itemView.findViewById(R.id.movie_title);
        movieReleaseDate = itemView.findViewById(R.id.movieReleaseDate);
        movieRuntime = itemView.findViewById(R.id.movieRuntime);
        movieVote = itemView.findViewById(R.id.movie_vote);
        moviePoster = itemView.findViewById(R.id.movie_poster_image_view);
        view = itemView;
    }

    public abstract void assignData(final Movie movie);

    protected void setBaseInformation(Movie movie) {
        movieTitle.setText(movie.getTitle());
        if (movie.getReleaseDate() != null) {
            movieReleaseDate.setText(convertDate(movie.getReleaseDate()));
            movieReleaseDate.setVisibility(View.VISIBLE);
        } else {
            movieReleaseDate.setVisibility(View.GONE);
        }
        movieVote.setText(resources.getString(R.string.vote, String.valueOf(movie.getVoteAverage())));
        setMoviePoster(movie);
    }

    private void setMoviePoster(Movie movie) {
        String posterName = movie.getPosterPath();
        String posterUri =
                Constants.POSTER_URI_SMALL
                        .replace("<posterName>", posterName != null ? posterName : "/");
        Picasso.with(context).load(posterUri).resize(222, 334).error(R.drawable.placeholder_poster).into(moviePoster);
    }

    protected String convertDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", resources.getConfiguration().locale);
        return simpleDateFormat.format(date);
    }
}
