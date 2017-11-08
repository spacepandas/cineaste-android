package de.cineaste.android.controllFlow;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.BaseWatchlistAdapter;
import de.cineaste.android.adapter.WatchlistAdapter;

/**
 * Created by marcelgross on 08.11.17.
 */

public class WatchlistItemTouchHelperCallback extends BaseItemTouchHelperCallback {

    public WatchlistItemTouchHelperCallback(LinearLayoutManager linearLayoutManager, BaseWatchlistAdapter baseWatchlistAdapter, RecyclerView recyclerView, Resources resources) {
        super(linearLayoutManager, baseWatchlistAdapter, recyclerView, resources);
    }

    @Override
    BaseSnackBar getSnackBar() {
        return new SnackBarWatchList(linearLayoutManager, (WatchlistAdapter) baseWatchlistAdapter, recyclerView);
    }

    @Override
    int getIcon() {
        return R.drawable.ic_add_to_watchedlist_white;
    }

    @Override
    int getRightSwipeMessage() {
        return R.string.move_not_seen;
    }
}
