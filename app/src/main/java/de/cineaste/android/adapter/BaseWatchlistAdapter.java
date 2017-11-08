package de.cineaste.android.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;

public abstract class BaseWatchlistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnMovieRemovedListener {

	MovieDbHelper db;
	List<Movie> dataset;
	List<Movie> filteredDataset;
	DisplayMessage displayMessage;

	public BaseWatchlistAdapter(DisplayMessage displayMessage, MovieDbHelper db) {
		this.displayMessage = displayMessage;
		this.db = db;
	}

	public abstract void updateDataSet();

	public interface DisplayMessage {
		void showMessageIfEmptyList(int messageId);
	}

	public void filter(String searchTerm) {
		if (filteredDataset == null)
			return;

		if (searchTerm != null && !searchTerm.isEmpty()) {
			for (Movie currentMovie : dataset) {
				String movieTitle = currentMovie.getTitle().toLowerCase();
				int index = filteredDataset.indexOf(currentMovie);
				if (!movieTitle.contains(searchTerm.toLowerCase())) {
					if (index != -1) {
						filteredDataset.remove(index);
						notifyItemRemoved(index);
					}
				} else {
					if (index == -1) {
						int location = indexInAlphabeticalOrder(currentMovie, filteredDataset);
						filteredDataset.add(location, currentMovie);
						notifyItemInserted(location);
					}
				}
			}
		} else {
			filteredDataset.clear();
			filteredDataset.addAll(dataset);
			notifyDataSetChanged();
		}
	}

	int indexInAlphabeticalOrder(Movie movie, List<Movie> movies) {
		for (int i = 0; i < movies.size(); ++i) {
			if (movie.compareTo(movies.get(i)) <= 0) {
				return i;
			}
		}
		return movies.size();
	}

	public void removeItem(int position) {
		Movie movie = filteredDataset.get(position);
		db.deleteMovieFromWatchlist(movie);
		removeMovie(movie);
	}
	public Movie getItem(int position) {
		return filteredDataset.get(position);
	}

	public void restoreDeletedItem(Movie item, int position) {
		db.createNewMovieEntry(item);
		filteredDataset.add(position, item);
		dataset.add(indexInAlphabeticalOrder(item, dataset), item);
		notifyItemInserted(position);
	}

	public void toggleItemOnList(Movie item) {
		item.setWatched(!item.isWatched());
		db.createOrUpdate(item);
		removeMovie(item);
	}

	public void restoreToggleItemOnList(Movie item, int position) {
		item.setWatched(!item.isWatched());
		db.createOrUpdate(item);
		filteredDataset.add(position, item);
		dataset.add(indexInAlphabeticalOrder(item, dataset), item);
		notifyItemInserted(position);
	}

	public int getDatasetSize() {
		return dataset.size();
	}
}