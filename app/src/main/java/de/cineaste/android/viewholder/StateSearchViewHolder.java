package de.cineaste.android.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.OnBackPressedListener;
import de.cineaste.android.entity.Movie;

public class StateSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
	private final TextView movieTitle;
	private Movie currentMovie;
	private final OnBackPressedListener listener;
	private final OnAddToListInSearchState addToListInSearchState;

	public interface OnAddToListInSearchState {
		void onAddToList(Movie currentMovie, int viewID);
	}

	public StateSearchViewHolder(View v, OnBackPressedListener listener, OnAddToListInSearchState addToListInSearchState) {
		super(v);
		movieTitle = (TextView) v.findViewById(R.id.movieTitle);
		TextView movieRuntime = (TextView) v.findViewById(R.id.movieRuntime);
		movieRuntime.setVisibility(View.GONE);
		ImageButton addToWatchList = (ImageButton) v.findViewById(R.id.addToWatchList);
		ImageButton addToWatchedList = (ImageButton) v.findViewById(R.id.addToWatchedList);
		this.listener = listener;
		this.addToListInSearchState = addToListInSearchState;

		addToWatchedList.setOnClickListener(this);
		addToWatchList.setOnClickListener(this);
	}

	public void assignData(final Movie movie) {
		currentMovie = movie;
		movieTitle.setText(movie.getTitle());
	}

	@Override
	public void onClick(View v) {
		addToListInSearchState.onAddToList(currentMovie, v.getId());

		listener.onBackPressedListener(currentMovie);
	}
}