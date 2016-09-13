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
import de.cineaste.android.entity.MovieAndState;
import de.cineaste.android.entity.MovieStateType;
import de.cineaste.android.fragment.BaseWatchlistFragment;
import de.cineaste.android.viewholder.WatchlistViewHolder;

public class WatchlistAdapter extends BaseWatchlistAdapter implements Observer {

    private final MovieDbHelper db;
    private final Context context;
    private final BaseWatchlistFragment baseFragment;
    private final MovieClickListener listener;

    public WatchlistAdapter(Context context, BaseWatchlistFragment baseFragment, MovieClickListener listener) {
        this.db = MovieDbHelper.getInstance(context);
        this.context = context;
        this.dataset = db.readMoviesByWatchStatus(false);
        this.filteredDataset = new LinkedList<>(dataset);
        this.baseFragment = baseFragment;
        this.db.addObserver(this);
        this.listener = listener;
    }

    @Override
    public void update(Observable observable, Object o) {
        MovieAndState movieAndState = (MovieAndState) o;
        MovieStateType state = movieAndState.getState();
        Movie changedMovie = movieAndState.getMovie();

        int index = dataset.indexOf(changedMovie);

        if(index == -1 && state != MovieStateType.INSERT) {
            return;
        }

        if (index == -1 && state == MovieStateType.INSERT) {
            if (!changedMovie.isWatched()) {
                dataset.add(indexInAlphabeticalOrder(changedMovie, dataset),
                        changedMovie);
                filter(oldSearchTerm);
                return;
            }
        }

        switch (state) {
            case STATUS_CHANGED:
            case DELETE:
                dataset.remove(index);
                int filterListIndex = filteredDataset.indexOf(changedMovie);
                filteredDataset.remove(filterListIndex);
                notifyItemRemoved(filterListIndex);
                filter(oldSearchTerm);
                break;
            case UPDATE:
                filteredDataset.set(index, changedMovie);
                notifyDataSetChanged();
                break;
        }

        if (dataset.size() == 0) {
            baseFragment.showMessageIfEmptyList(R.string.noMoviesOnWatchList);
        }
    }

    @Override
    public WatchlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_watchlist, parent, false);
        return new WatchlistViewHolder(v, db, context, listener);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((WatchlistViewHolder) holder).assignData(filteredDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }

}
