package de.cineaste.android.adapter.movie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.viewholder.movie.MovieSearchViewHolder;

public class MovieSearchQueryAdapter extends RecyclerView.Adapter<MovieSearchViewHolder> {
	private final List<Movie> dataset = new ArrayList<>();
	private final ItemClickListener listener;
	private final OnMovieStateChange movieStateChange;

	public interface OnMovieStateChange {
		void onMovieStateChangeListener(Movie movie, int viewId, int index);
	}


	public MovieSearchQueryAdapter(ItemClickListener listener, OnMovieStateChange movieStateChange) {
		this.listener = listener;
		this.movieStateChange = movieStateChange;
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
	public MovieSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater
				.from(parent.getContext())
				.inflate(R.layout.card_movie_search, parent, false);
		return new MovieSearchViewHolder(v, parent.getContext(), movieStateChange, listener);
	}

	@Override
	public void onBindViewHolder(final MovieSearchViewHolder holder, final int position) {
		holder.assignData(dataset.get(position));
	}

	@Override
	public int getItemCount() {
		return dataset.size();
	}

}

