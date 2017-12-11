package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.cineaste.android.R;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.fragment.WatchState;
import de.cineaste.android.listener.MovieClickListener;
import de.cineaste.android.listener.OnMovieRemovedListener;
import de.cineaste.android.viewholder.MovieViewHolder;

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnMovieRemovedListener, Filterable {

    private final Context context;
    private final MovieDbHelper db;
    private final DisplayMessage displayMessage;
    private final MovieClickListener listener;
    private final WatchState state;
    private List<Movie> dataSet = new ArrayList<>();
    private List<Movie> filteredDataSet;

    private final Queue<UpdatedMovies> updatedMovies = new LinkedBlockingQueue<>();

    public MovieListAdapter(DisplayMessage displayMessage, Context context, MovieClickListener listener, WatchState state) {
        this.displayMessage = displayMessage;
        this.db = MovieDbHelper.getInstance(context);
        this.context = context;
        this.listener = listener;
        this.state = state;
        this.dataSet.clear();
        this.dataSet.addAll(db.readMoviesByWatchStatus(state));
        this.filteredDataSet = new LinkedList<>(dataSet);
    }

    public interface DisplayMessage {
        void showMessageIfEmptyList();
    }

    @Override
    public Filter getFilter() {
        return new FilterMovies(this, dataSet);
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
        db.createOrUpdate(item);
        filteredDataSet.add(position, item);
        dataSet.add(position, item);
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
        dataSet.add(position, item);
        notifyItemInserted(position);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        updateMoviePositionsAndAddToQueue(fromPosition, toPosition);

        Collections.swap(filteredDataSet, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void updateMoviePositionsAndAddToQueue(int fromPosition, int toPosition) {
        Movie passiveMovedMovie = filteredDataSet.remove(toPosition);
        passiveMovedMovie.setListPosition(fromPosition);
        filteredDataSet.add(toPosition, passiveMovedMovie);

        Movie prev = filteredDataSet.remove(fromPosition);
        prev.setListPosition(toPosition);
        filteredDataSet.add(fromPosition, prev);
        updatedMovies.add(new UpdatedMovies(prev, passiveMovedMovie));
    }

    public void orderAlphabetical() {
        List<Movie> movies = db.reorderAlphabetical(state);
        dataSet = movies;
        filteredDataSet = movies;
    }

    public void orderByReleaseDate() {
        List<Movie> movies = db.reorderByReleaseDate(state);
        dataSet = movies;
        filteredDataSet = movies;
    }

    public void updatePositionsInDb() {
        while (updatedMovies.iterator().hasNext()) {
            UpdatedMovies movies = updatedMovies.poll();
            db.updatePosition(movies.getPrev());
            db.updatePosition(movies.getPassiveMovie());
        }

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

    public class FilterMovies extends Filter {

        private final MovieListAdapter adapter;
        final List<Movie> movieList;
        final List<Movie> filteredMovieList;

        FilterMovies(MovieListAdapter adapter, List<Movie> movieList) {
            this.adapter = adapter;
            this.movieList = movieList;
            this.filteredMovieList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredMovieList.clear();
            final FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filteredMovieList.addAll(movieList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (Movie movie : movieList) {
                    if (movie.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredMovieList.add(movie);
                    }
                }
            }

            results.values = filteredMovieList;
            results.count = filteredMovieList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            adapter.filteredDataSet.clear();
            //noinspection unchecked
            adapter.filteredDataSet.addAll((List<Movie>) results.values);
            adapter.notifyDataSetChanged();
        }
    }

    public class UpdatedMovies {
        private Movie prev;
        private Movie passiveMovie;

        public UpdatedMovies(Movie prev, Movie passiveMovie) {
            this.prev = prev;
            this.passiveMovie = passiveMovie;
        }

        public Movie getPrev() {
            return prev;
        }

        public Movie getPassiveMovie() {
            return passiveMovie;
        }
    }

}