package de.cineaste.android.controllFlow.movie;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.movie.MovieListAdapter;
import de.cineaste.android.controllFlow.BaseSnackBar;

public class HistorylistMovieTouchHelperCallback extends BaseMovieTouchHelperCallback {

    public HistorylistMovieTouchHelperCallback(LinearLayoutManager linearLayoutManager, MovieListAdapter movieListAdapter, RecyclerView recyclerView, Resources resources) {
        super(linearLayoutManager, movieListAdapter, recyclerView, resources);
    }

    @Override
    protected int getIcon() {
        return R.drawable.ic_add_to_watchlist_white;
    }

    @Override
    protected BaseSnackBar getSnackBar() {
        return new MovieSnackBarHistory(linearLayoutManager, movieListAdapter, recyclerView);
    }

    @Override
    protected int getRightSwipeMessage() {
        return R.string.not_seen;
    }
}
