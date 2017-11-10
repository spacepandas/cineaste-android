package de.cineaste.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.cineaste.android.Constants;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;

public class SearchQueryAdapter extends RecyclerView.Adapter<SearchQueryAdapter.ViewHolder> {
	private final List<Movie> dataset = new ArrayList<>();
	private final MovieClickListener listener;
	private final OnMovieStateChange movieStateChange;
	private final Resources resources;

	public interface OnMovieStateChange {
		void onMovieStateChangeListener(Movie movie, int viewId, int index);
	}


	public SearchQueryAdapter(MovieClickListener listener, OnMovieStateChange movieStateChange, Resources resources) {
		this.listener = listener;
		this.movieStateChange = movieStateChange;
		this.resources = resources;
	}

	public void addMovies(List<Movie> movies) {
		dataset.clear();
		dataset.addAll(movies);
		notifyDataSetChanged();
	}

	public void addMovie(Movie movie, int index) {
		dataset.add(index, movie);
	}

	public void removeMovie(int index) {
		dataset.remove(index);
		notifyItemRemoved(index);
	}

	@Override
	public SearchQueryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater
				.from(parent.getContext())
				.inflate(R.layout.card_movie_search_query, parent, false);
		return new ViewHolder(v, parent.getContext(), movieStateChange);
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
		final TextView movieReleaseDate;
		final TextView movieRuntime;
		final ImageView moviePoster;
		final Button addToWatchlistButton;
		final Button movieWatchedButton;
		final View view;
		final OnMovieStateChange movieStateChange;
		final Context context;

		public ViewHolder(View v, Context context, OnMovieStateChange movieStateChange) {
			super(v);
			this.movieStateChange = movieStateChange;
			this.context = context;
			movieTitle = v.findViewById(R.id.movie_title);
			movieReleaseDate = v.findViewById(R.id.movieReleaseDate);
			movieRuntime = v.findViewById(R.id.movieRuntime);
			movieRuntime.setVisibility(View.GONE);
			movieVote = v.findViewById(R.id.movie_vote);
			moviePoster = v.findViewById(R.id.movie_poster_image_view);
			addToWatchlistButton = v.findViewById(R.id.to_watchlist_button);
			movieWatchedButton = v.findViewById(R.id.watched_button);
			view = v;
		}

		public void assignData(final Movie movie) {
			Resources resources = context.getResources();
			movieTitle.setText(movie.getTitle());
			if (movie.getReleaseDate() != null) {
				movieReleaseDate.setVisibility(View.VISIBLE);
				movieReleaseDate.setText(convertDate(movie.getReleaseDate()));
			} else {
				movieReleaseDate.setVisibility(View.GONE);
			}
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
					movieStateChange.onMovieStateChangeListener(movie, v.getId(), index);
				}
			});

			movieWatchedButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = dataset.indexOf(movie);
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

	private String convertDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", resources.getConfiguration().locale);
		return simpleDateFormat.format(date);
	}
}

