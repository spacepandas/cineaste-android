package de.cineaste.android.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.OnBackPressedListener;
import de.cineaste.android.entity.Movie;

public class StateWatchedListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
	private final TextView movieTitle;
	private final TextView movieRuntime;
	private Movie currentMovie;
	private final Context context;
	private final OnBackPressedListener listener;
	private final OnMovieRemovedFromWatchedList onMovieRemoved;

	public interface OnMovieRemovedFromWatchedList {
		void movieRemoved(Movie movie);
	}

	public StateWatchedListViewHolder(View v, Context context, OnBackPressedListener listener, OnMovieRemovedFromWatchedList onMovieRemoved) {
		super(v);
		this.context = context;
		movieTitle = (TextView) v.findViewById(R.id.movieTitle);
		movieRuntime = (TextView) v.findViewById(R.id.movieRuntime);
		ImageButton delete = (ImageButton) v.findViewById(R.id.remove);
		this.listener = listener;
		this.onMovieRemoved = onMovieRemoved;
		delete.setOnClickListener(this);
	}

	public void assignData(final Movie movie) {
		Resources resources = context.getResources();
		currentMovie = movie;
		movieTitle.setText(movie.getTitle());
		movieRuntime.setText(resources.getString(R.string.runtime, movie.getRuntime()));
	}

	@Override
	public void onClick(View v) {
		onMovieRemoved.movieRemoved(currentMovie);
		listener.onBackPressedListener();
	}
}