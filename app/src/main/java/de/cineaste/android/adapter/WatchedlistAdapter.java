package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.fragment.BaseWatchlistFragment;
import de.cineaste.android.viewholder.WatchedlistViewHolder;

public class WatchedlistAdapter extends BaseWatchlistAdapter implements Observer {

    private final MovieDbHelper db;
    private final Context context;
    private final BaseWatchlistFragment baseFragment;
    private final MovieClickListener listener;

    public WatchedlistAdapter(Context context, BaseWatchlistFragment baseFragment, MovieClickListener listener) {
        this.db = MovieDbHelper.getInstance(context);
        this.context = context;
        this.db.addObserver(this);
        this.dataset = db.readMoviesByWatchStatus(true);
        this.filteredDataset = new LinkedList<>(dataset);
        this.baseFragment = baseFragment;
        this.listener = listener;
    }

    @Override
    public void update(Observable observable, Object data) {
        Movie changedMovie = (Movie) data;
        int index = dataset.indexOf(changedMovie);
        if ((!changedMovie.isWatched() && index != -1) ||
                (changedMovie.isWatched() && index != -1)) {
            dataset.remove(index);
            filter(oldSearchTerm);
        } else if (changedMovie.isWatched() && index == -1) {
            dataset.add(indexInAlphabeticalOrder(changedMovie, dataset), changedMovie);
            filter(oldSearchTerm);
        }
        baseFragment.showMessageIfEmptyList(R.string.noMoviesOnWatchedList);
    }

    public WatchedlistViewHolder onCreateViewHolder(ViewGroup parent, int viewTyp) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_watchedlist, parent, false);
        return new WatchedlistViewHolder(v, context, db, listener);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((WatchedlistViewHolder) holder).assignData(filteredDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }

}