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

public class WatchedlistViewHolder extends RecyclerView.ViewHolder {
	private final Context context;
	private final MovieDbHelper db;
	private final MovieClickListener listener;

	private final TextView movieTitle;
	private final TextView movieRuntime;
	private final TextView movieVote;
	private final ImageButton removeMovie;
	private final ImageView imageView;
	private final View view;

	public WatchedlistViewHolder(View v, Context context, MovieDbHelper db, MovieClickListener listener) {
		super(v);
		movieTitle = (TextView) v.findViewById(R.id.movie_title);
		movieRuntime = (TextView) v.findViewById(R.id.movieRuntime);
		movieVote = (TextView) v.findViewById(R.id.movie_vote);
		removeMovie = (ImageButton) v.findViewById(R.id.remove_button);
		imageView = (ImageView) v.findViewById(R.id.movie_poster_image_view);
		view = v;
		this.context = context;
		this.db = db;
		this.listener = listener;
	}

	public void assignData(final Movie movie) {
		Resources resources = context.getResources();

		movieTitle.setText(movie.getTitle());
		movieRuntime.setText(resources.getString(R.string.runtime, movie.getRuntime()));
		movieVote.setText(resources.getString(R.string.vote, String.valueOf(movie.getVoteAverage())));
		String posterName = movie.getPosterPath();
		String posterUri = Constants.POSTER_URI_SMALL
				.replace("<posterName>", posterName != null ? posterName : "/");
		Picasso.with(context).load(posterUri).resize(222, 334).error(R.drawable.placeholder_poster).into(imageView);

		removeMovie.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				db.deleteMovieFromWatchlist(movie);
			}
		});

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null) {
					listener.onMovieClickListener(movie.getId(),
							new View[]{view, imageView, movieTitle, movieRuntime, movieVote});
				}
			}
		});
	}
}
