package de.cineaste.android.controllFlow;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.MovieListAdapter;

public class WatchlistItemTouchHelperCallback extends BaseItemTouchHelperCallback {

    public WatchlistItemTouchHelperCallback(LinearLayoutManager linearLayoutManager, MovieListAdapter movieListAdapter, RecyclerView recyclerView, Resources resources) {
        super(linearLayoutManager, movieListAdapter, recyclerView, resources);
    }

    @Override
    BaseSnackBar getSnackBar() {
        return new SnackBarWatchList(linearLayoutManager, movieListAdapter, recyclerView);
    }

    @Override
    int getIcon() {
        return R.drawable.ic_add_to_watchedlist_white;
    }

    @Override
    int getRightSwipeMessage() {
        return R.string.move_seen;
    }
}
