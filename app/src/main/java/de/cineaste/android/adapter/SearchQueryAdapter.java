package de.cineaste.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.cineaste.android.Constants;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.TheMovieDb;

public class SearchQueryAdapter extends RecyclerView.Adapter<SearchQueryAdapter.ViewHolder> {
	public List<Movie> dataset;
	private final MovieDbHelper db;
	private final Context context;
	private final TheMovieDb theMovieDb;
	private final MovieClickListener listener;


	public SearchQueryAdapter(Context context, List<Movie> movies, MovieClickListener listener) {
		db = MovieDbHelper.getInstance(context);
		this.context = context;
		dataset = movies;
		theMovieDb = new TheMovieDb();
		this.listener = listener;
	}

	@Override
	public SearchQueryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater
				.from(parent.getContext())
				.inflate(R.layout.card_movie_search_query, parent, false);
		return new ViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position) {
		holder.assignData(dataset.get(position));
	}

	@Override
	public int getItemCount() {
		return dataset.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		final TextView movieVote;
		final TextView movieTitle;
		final TextView movieRuntime;
		final ImageView moviePoster;
		final ImageButton addToWatchlistButton;
		final ImageButton movieWatchedButton;
		final View view;

		public ViewHolder(View v) {
			super(v);
			movieTitle = (TextView) v.findViewById(R.id.movie_title);
			movieRuntime = (TextView) v.findViewById(R.id.movieRuntime);
			movieRuntime.setVisibility(View.GONE);
			movieVote = (TextView) v.findViewById(R.id.movie_vote);
			moviePoster = (ImageView) v.findViewById(R.id.movie_poster_image_view);
			addToWatchlistButton = (ImageButton) v.findViewById(R.id.to_watchlist_button);
			movieWatchedButton = (ImageButton) v.findViewById(R.id.watched_button);
			view = v;
		}

		public void assignData(final Movie movie) {
			Resources resources = context.getResources();
			movieTitle.setText(movie.getTitle());
			movieVote.setText(resources.getString(R.string.vote, String.valueOf(movie.getVoteAverage())));
			String posterName = movie.getPosterPath();
			String posterUri =
					Constants.POSTER_URI_SMALL
							.replace("<posterName>", posterName != null ? posterName : "/");
			Picasso.with(context).load(posterUri).resize(222, 334).error(R.drawable.placeholder_poster).into(moviePoster);

			addToWatchlistButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = dataset.indexOf(movie);

					theMovieDb.fetchMovie(
							movie.getId(),
							context.getResources().getString(R.string.language_tag),
							new TheMovieDb.OnFetchMovieResultListener() {
								@Override
								public void onFetchMovieResultListener(Movie movie) {
									db.createNewMovieEntry(movie);
								}
							});

					dataset.remove(index);
					notifyItemRemoved(index);
				}
			});

			movieWatchedButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = dataset.indexOf(movie);

					theMovieDb.fetchMovie(
							movie.getId(),
							context.getResources().getString(R.string.language_tag),
							new TheMovieDb.OnFetchMovieResultListener() {
								@Override
								public void onFetchMovieResultListener(Movie movie) {
									movie.setWatched(true);
									db.createNewMovieEntry(movie);
								}
							});

					dataset.remove(index);
					notifyItemRemoved(index);
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
}

