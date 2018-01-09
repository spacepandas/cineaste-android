package de.cineaste.android.controllFlow.series;


import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesListAdapter;
import de.cineaste.android.controllFlow.BaseSnackBar;

public class HistoryListSeriesTouchHelperCallback extends BaseSeriesTouchHelperCallback {

    public HistoryListSeriesTouchHelperCallback(Resources resources, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView, SeriesListAdapter seriesListAdapter) {
        super(resources, linearLayoutManager, recyclerView, seriesListAdapter);
    }

    @Override
    protected int getIcon() {
        return R.drawable.ic_add_to_watchlist_white;
    }

    @Override
    protected BaseSnackBar getSnackBar() {
        return new SeriesSnackBarHistory(linearLayoutManager, recyclerView, seriesListAdapter);
    }

    @Override
    protected int getRightSwipeMessage() {
        return R.string.not_seen;
    }
}
