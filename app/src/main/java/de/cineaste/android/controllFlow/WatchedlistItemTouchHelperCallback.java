package de.cineaste.android.controllFlow;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.MovieListAdapter;

public class WatchedlistItemTouchHelperCallback extends BaseItemTouchHelperCallback {

    public WatchedlistItemTouchHelperCallback(LinearLayoutManager linearLayoutManager, MovieListAdapter movieListAdapter, RecyclerView recyclerView, Resources resources) {
        super(linearLayoutManager, movieListAdapter, recyclerView, resources);
    }

    @Override
    int getIcon() {
        return R.drawable.ic_add_to_watchlist_white;
    }

    @Override
    BaseSnackBar getSnackBar() {
        return new SnackBarWatchedList(linearLayoutManager, movieListAdapter, recyclerView);
    }

    @Override
    int getRightSwipeMessage() {
        return R.string.move_not_seen;
    }
}
