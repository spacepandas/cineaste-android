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
import de.cineaste.android.fragment.WatchState;
import de.cineaste.android.viewholder.MovieViewHolder;

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnMovieRemovedListener {

    private final Context context;
    private final MovieDbHelper db;
    private final DisplayMessage displayMessage;
    private final MovieClickListener listener;
    private final WatchState state;
    private List<Movie> dataSet;
    private List<Movie> filteredDataSet;

    public MovieListAdapter(DisplayMessage displayMessage, Context context, MovieClickListener listener, WatchState state) {
        this.displayMessage = displayMessage;
        this.db = MovieDbHelper.getInstance(context);
        this.context = context;
        this.listener = listener;
        this.state = state;
        this.dataSet = db.readMoviesByWatchStatus(state);
        this.filteredDataSet = new LinkedList<>(dataSet);
    }

    public interface DisplayMessage {
        void showMessageIfEmptyList();
    }

    public void filter(String searchTerm) {
        if (filteredDataSet == null)
            return;

        if (searchTerm != null && !searchTerm.isEmpty()) {
            for (Movie currentMovie : dataSet) {
                String movieTitle = currentMovie.getTitle().toLowerCase();
                int index = filteredDataSet.indexOf(currentMovie);
                if (!movieTitle.contains(searchTerm.toLowerCase())) {
                    if (index != -1) {
                        filteredDataSet.remove(index);
                        notifyItemRemoved(index);
                    }
                } else {
                    if (index == -1) {
                        int location = indexInAlphabeticalOrder(currentMovie, filteredDataSet);
                        filteredDataSet.add(location, currentMovie);
                        notifyItemInserted(location);
                    }
                }
            }
        } else {
            filteredDataSet.clear();
            filteredDataSet.addAll(dataSet);
            notifyDataSetChanged();
        }
    }

    private int indexInAlphabeticalOrder(Movie movie, List<Movie> movies) {
        for (int i = 0; i < movies.size(); ++i) {
            if (movie.compareTo(movies.get(i)) <= 0) {
                return i;
            }
        }
        return movies.size();
    }

    public void removeItem(int position) {
        Movie movie = filteredDataSet.get(position);
        db.deleteMovieFromWatchlist(movie);
        removeMovie(movie);
    }

    public Movie getItem(int position) {
        return filteredDataSet.get(position);
    }

    public void restoreDeletedItem(Movie item, int position) {
        db.createNewMovieEntry(item);
        filteredDataSet.add(position, item);
        dataSet.add(indexInAlphabeticalOrder(item, dataSet), item);
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
        filteredDataSet.add(position, item);
        dataSet.add(indexInAlphabeticalOrder(item, dataSet), item);
        notifyItemInserted(position);
    }

    public int getDataSetSize() {
        return dataSet.size();
    }

    @Override
    public int getItemCount() {
        return filteredDataSet.size();
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
        ((MovieViewHolder) holder).assignData(filteredDataSet.get(position));
    }

    @Override
    public void removeMovie(Movie movie) {
        notifyItemRemoved(filteredDataSet.indexOf(movie));

        dataSet.remove(movie);
        filteredDataSet.remove(movie);
        displayMessage.showMessageIfEmptyList();
    }

    public void updateDataSet() {
        this.dataSet = db.readMoviesByWatchStatus(state);
        this.filteredDataSet = new LinkedList<>(dataSet);
        displayMessage.showMessageIfEmptyList();
        notifyDataSetChanged();
    }
}