package de.cineaste.android.viewholder;


import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.cineaste.android.Constants;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;

public class WatchlistViewHolder extends RecyclerView.ViewHolder {
	private final MovieDbHelper db;
	private final Context context;
	private final MovieClickListener listener;

	public final TextView movieTitle;
	public final TextView movieRuntime;
	public final TextView movieVote;
	public final ImageView imageView;
	public final ImageButton removeMovieButton;
	public final ImageButton movieWatchedButton;
	final View view;

	public WatchlistViewHolder(View v, MovieDbHelper db, Context context, MovieClickListener listener) {
		super(v);
		movieTitle = (TextView) v.findViewById(R.id.movie_title);
		movieRuntime = (TextView) v.findViewById(R.id.movieRuntime);
		movieVote = (TextView) v.findViewById(R.id.movie_vote);
		removeMovieButton = (ImageButton) v.findViewById(R.id.remove_button);
		movieWatchedButton = (ImageButton) v.findViewById(R.id.watched_button);
		imageView = (ImageView) v.findViewById(R.id.movie_poster_image_view);
		view = v;
		this.db = db;
		this.context = context;
		this.listener = listener;
	}

	public void assignData(final Movie movie) {
		Resources resources = context.getResources();

		movieTitle.setText(movie.getTitle());
		movieRuntime.setText(resources.getString(R.string.runtime, movie.getRuntime()));
		movieVote.setText(resources.getString(R.string.vote, movie.getVoteAverage()));
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
			}
		});

		movieWatchedButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				movie.setWatched(true);
				db.createOrUpdate(movie);
			}
		});
	}
}
