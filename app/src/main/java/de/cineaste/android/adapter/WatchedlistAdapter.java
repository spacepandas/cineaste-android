package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.viewholder.WatchedlistViewHolder;

public class WatchedlistAdapter extends BaseWatchlistAdapter {

    private final MovieDbHelper db;
    private final Context context;
    private final MovieClickListener listener;

    public WatchedlistAdapter(Context context, MovieClickListener listener, DisplayMessage displayMessage) {
        super(displayMessage);
        this.db = MovieDbHelper.getInstance(context);
        this.context = context;
        this.dataset = db.readMoviesByWatchStatus(true);
        this.filteredDataset = new LinkedList<>(dataset);
        this.listener = listener;
    }

    @Override
    public void removeMovie(Movie movie) {
        notifyItemRemoved(filteredDataset.indexOf(movie));

        dataset.remove(movie);
        filteredDataset.remove(movie);
        displayMessage.showMessageIfEmptyList(R.string.noMoviesOnWatchedList);
    }

    @Override
    public void updateDataSet() {
        this.dataset = db.readMoviesByWatchStatus(true);
        this.filteredDataset = new LinkedList<>(dataset);
        displayMessage.showMessageIfEmptyList(R.string.noMoviesOnWatchedList);
        notifyDataSetChanged();
    }

    public WatchedlistViewHolder onCreateViewHolder(ViewGroup parent, int viewTyp) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_watchedlist, parent, false);
        return new WatchedlistViewHolder(v, context, db, listener);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((WatchedlistViewHolder) holder).assignData(filteredDataset.get(position), this);
    }

    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }

}