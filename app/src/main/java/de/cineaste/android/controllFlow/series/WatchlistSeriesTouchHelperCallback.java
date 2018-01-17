package de.cineaste.android.controllFlow.series;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesListAdapter;
import de.cineaste.android.controllFlow.BaseSnackBar;

public class WatchlistSeriesTouchHelperCallback extends BaseSeriesTouchHelperCallback {

    private Context context;

    public WatchlistSeriesTouchHelperCallback(Resources resources, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView, SeriesListAdapter seriesListAdapter, Context context) {
        super(resources, linearLayoutManager, recyclerView, seriesListAdapter);
        this.context = context;
    }

    @Override
    protected BaseSnackBar getSnackBar() {
        return new SeriesSnackBarWatchList(linearLayoutManager, recyclerView, seriesListAdapter, context);
    }

    @Override
    protected int getIcon() {
        return R.drawable.ic_add_to_history_white;
    }

    @Override
    protected int getRightSwipeMessage() {
        return R.string.series_seen;
    }
}
