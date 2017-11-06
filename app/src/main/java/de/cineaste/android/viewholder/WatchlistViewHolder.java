package de.cineaste.android.viewholder;


import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cineaste.android.Constants;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.adapter.BaseWatchlistAdapter;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;

public class WatchlistViewHolder extends RecyclerView.ViewHolder {
	private final MovieDbHelper db;
	private final Context context;
	private final MovieClickListener listener;

	private final TextView movieTitle;
	private final TextView movieReleaseDate;
	private final TextView movieRuntime;
	private final TextView movieVote;
	private final ImageView imageView;
	private final ImageButton removeMovieButton;
	private final ImageButton movieWatchedButton;
	private final View view;

	public WatchlistViewHolder(View v, MovieDbHelper db, Context context, MovieClickListener listener) {
		super(v);
		movieTitle = v.findViewById(R.id.movie_title);
		movieReleaseDate = v.findViewById(R.id.movieReleaseDate);
		movieRuntime = v.findViewById(R.id.movieRuntime);
		movieVote = v.findViewById(R.id.movie_vote);
		removeMovieButton = v.findViewById(R.id.remove_button);
		movieWatchedButton = v.findViewById(R.id.watched_button);
		imageView = v.findViewById(R.id.movie_poster_image_view);
		view = v;
		this.db = db;
		this.context = context;
		this.listener = listener;
	}

	public void assignData(final Movie movie, final BaseWatchlistAdapter adapter) {
		Resources resources = context.getResources();

		movieTitle.setText(movie.getTitle());
		if (movie.getReleaseDate() != null) {
			movieReleaseDate.setText(convertDate(movie.getReleaseDate()));
			movieReleaseDate.setVisibility(View.VISIBLE);
		} else {
			movieReleaseDate.setVisibility(View.GONE);
		}
		movieRuntime.setText(resources.getString(R.string.runtime, movie.getRuntime()));
		movieVote.setText(resources.getString(R.string.vote, String.valueOf(movie.getVoteAverage())));
		String posterName = movie.getPosterPath();
		String posterUri = Constants.POSTER_URI_SMALL.replace("<posterName>", posterName != null ? posterName : "/");
		Picasso.with(context).load(posterUri).resize(222, 334).error(R.drawable.placeholder_poster).into(imageView);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onMovieClickListener(movie.getId(),
							new View[]{view, imageView, movieTitle, movieRuntime, movieVote});
			}
		});

		removeMovieButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				db.deleteMovieFromWatchlist(movie);
				adapter.removeMovie(movie);
			}
		});

		movieWatchedButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				movie.setWatched(true);
				db.update(movie);
				adapter.removeMovie(movie);
			}
		});
	}

	private String convertDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", context.getResources().getConfiguration().locale);
		return simpleDateFormat.format(date);
	}
}
