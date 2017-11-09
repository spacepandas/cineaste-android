package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.fragment.BaseWatchlistFragment;
import de.cineaste.android.viewholder.MovieViewHolder;

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnMovieRemovedListener {

    Context context;
	MovieDbHelper db;
	List<Movie> dataset;
	List<Movie> filteredDataset;
	DisplayMessage displayMessage;
	MovieClickListener listener;
	String watchListState;

	public MovieListAdapter(DisplayMessage displayMessage, Context context, MovieClickListener listener, String watchListState) {
		this.displayMessage = displayMessage;
		this.db = MovieDbHelper.getInstance(context);
		this.context = context;
		this.listener = listener;
		this.watchListState = watchListState;
		this.dataset = fillMovieListByState();
		this.filteredDataset = new LinkedList<>(dataset);
	}

	public interface DisplayMessage {
		void showMessageIfEmptyList(int messageId);
	}

	private List<Movie> fillMovieListByState() {
	    if (watchListState.equals(BaseWatchlistFragment.WatchlistFragmentType.WATCH_LIST)) {
            return db.readMoviesByWatchStatus(false);
        } else {
            return db.readMoviesByWatchStatus(true);
        }
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

    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_movie, parent, false);
        return new MovieViewHolder(v, context, listener);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((MovieViewHolder) holder).assignData(filteredDataset.get(position));
    }

	@Override
	public void removeMovie(Movie movie) {
		notifyItemRemoved(filteredDataset.indexOf(movie));

		dataset.remove(movie);
		filteredDataset.remove(movie);
		displayMessage.showMessageIfEmptyList(getEmptyListMessageByState());
	}

	private int getEmptyListMessageByState() {
		if (watchListState.equals(BaseWatchlistFragment.WatchlistFragmentType.WATCH_LIST)) {
			return R.string.noMoviesOnWatchList;
		} else {
			return R.string.noMoviesOnWatchedList;
		}
	}

	public void updateDataSet() {
		this.dataset = fillMovieListByState();
		this.filteredDataset = new LinkedList<>(dataset);
		displayMessage.showMessageIfEmptyList(R.string.noMoviesOnWatchList);
		notifyDataSetChanged();
	}


}