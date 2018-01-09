package de.cineaste.android.controllFlow.series;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import de.cineaste.android.adapter.series.SeriesListAdapter;
import de.cineaste.android.controllFlow.TouchHelperCallback;
import de.cineaste.android.viewholder.series.SeriesViewHolder;

public abstract class BaseSeriesTouchHelperCallback extends TouchHelperCallback {

    final SeriesListAdapter seriesListAdapter;

    public BaseSeriesTouchHelperCallback(Resources resources, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView, SeriesListAdapter seriesListAdapter) {
        super(resources, linearLayoutManager, recyclerView);
        this.seriesListAdapter = seriesListAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        seriesListAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            SeriesViewHolder seriesViewHolder = (SeriesViewHolder) viewHolder;
            seriesViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        SeriesViewHolder seriesViewHolder = (SeriesViewHolder) viewHolder;
        seriesViewHolder.onItemClear();

        seriesListAdapter.updatePositionsInDb();
    }
}
