package de.cineaste.android.adapter.series;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.cineaste.android.R;
import de.cineaste.android.adapter.BaseListAdapter;
import de.cineaste.android.database.dbHelper.EpisodeDbHelper;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.entity.series.Season;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.fragment.WatchState;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.viewholder.series.SeriesViewHolder;


public class SeriesListAdapter extends BaseListAdapter {

    private final SeriesDbHelper db;
    private final EpisodeDbHelper episodeDbHelper;
    private List<Series> dataSet = new ArrayList<>();
    private List<Series> filteredDataSet;
    private OnEpisodeWatchedClickListener onEpisodeWatchedClickListener;

    private final Queue<UpdatedSeries> updatedSeries = new LinkedBlockingQueue<>();

    public SeriesListAdapter(DisplayMessage displayMessage, Context context, ItemClickListener listener, WatchState state, OnEpisodeWatchedClickListener onEpisodeWatchedClickListener) {
        super(context, displayMessage, listener, state);
        this.onEpisodeWatchedClickListener = onEpisodeWatchedClickListener;
        this.dataSet.clear();
        this.db = SeriesDbHelper.getInstance(context);
        this.episodeDbHelper = EpisodeDbHelper.getInstance(context);
        this.dataSet.addAll(db.readSeriesByWatchStatus(state));
        this.filteredDataSet = new LinkedList<>(dataSet);
    }

    public interface OnEpisodeWatchedClickListener {
        void onEpisodeWatchedClick(Series series, int position);
    }

    @Override
    @NonNull
    protected Filter getInternalFilter() {
        return new FilerSeries(this, dataSet);
    }

    public void removeItem(int position) {
        Series series = filteredDataSet.get(position);
        db.delete(series.getId());
        removeSeriesFromList(series);
    }

    public Series getItem(int position) {
        return filteredDataSet.get(position);
    }

    public void updateSeries(Series series, int pos) {
        series = db.readSeries(series.getId());
        dataSet.remove(pos);
        filteredDataSet.remove(pos);
        if (state == WatchState.WATCH_STATE && !series.isWatched()) {
            dataSet.add(pos, series);
            filteredDataSet.add(pos, series);
            notifyItemChanged(pos);
        } else {
            notifyItemRemoved(pos);
        }
    }

    public void restoreDeletedItem(Series item, int position) {
        db.createOrUpdate(item);
        addSeriesToList(item, position);
    }

    public void toggleItemOnList(Series item) {
        item.setWatched(!item.isWatched());
        db.createOrUpdate(item);
    }
    public void restoreToggleItemOnList(Series item, int position) {
        item.setWatched(!item.isWatched());
        db.createOrUpdate(item);
        addSeriesToList(item, position);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        updateSeriesPositionsAndAddToQueue(fromPosition, toPosition);

        Collections.swap(filteredDataSet, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void updateSeriesPositionsAndAddToQueue(int fromPosition, int toPosition) {
        Series passiveMovedSeries = filteredDataSet.remove(toPosition);
        passiveMovedSeries.setListPosition(fromPosition);
        filteredDataSet.add(toPosition, passiveMovedSeries);

        Series prev = filteredDataSet.remove(fromPosition);
        prev.setListPosition(toPosition);
        filteredDataSet.add(fromPosition, prev);

        updatedSeries.add(new UpdatedSeries(prev, passiveMovedSeries));
    }

    public void orderAlphabetical() {
        List<Series> series = db.reorderAlphabetical(state);
        dataSet = series;
        filteredDataSet = series;
    }

    public void orderByReleaseDate() {
        List<Series> series = db.reorderByReleaseDate(state);
        dataSet = series;
        filteredDataSet = series;
    }

    public void updatePositionsInDb() {
        while (updatedSeries.iterator().hasNext()) {
            UpdatedSeries series = updatedSeries.poll();

            db.updatePosition(series.getPrev());
            db.updatePosition(series.getPassiveSeries());
        }

    }

    public int getDataSetSize() {
        return dataSet.size();
    }

    @Override
    public int getItemCount() {
        return filteredDataSet.size();
    }

    @Override
    @NonNull
    protected RecyclerView.ViewHolder createViewHolder(View v) {
        return new SeriesViewHolder(v, listener, context, state, onEpisodeWatchedClickListener);
    }

    @Override
    protected int getLayout() {
        return R.layout.card_series;
    }

    protected void assignDataToViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SeriesViewHolder) holder).assignData(filteredDataSet.get(position), position);
    }

    public void removeSeriesFromList(Series series) {
        notifyItemRemoved(filteredDataSet.indexOf(series));

        dataSet.remove(series);
        filteredDataSet.remove(series);
        displayMessage.showMessageIfEmptyList();
    }

    public void addSeriesToList(Series series, int position) {
        filteredDataSet.add(position, series);
        dataSet.add(position, series);
        notifyItemInserted(position);
    }

    public void updateDataSet() {
        this.dataSet = db.readSeriesByWatchStatus(state);
        this.filteredDataSet = new LinkedList<>(dataSet);
        displayMessage.showMessageIfEmptyList();
        notifyDataSetChanged();
    }

    public void markEpisodes(Series series, WatchState watchState) {
        boolean watchStatus = false;
        if (watchState == WatchState.WATCHED_STATE) {
            watchStatus = true;
        }

        for (Season season : series.getSeasons()) {
            episodeDbHelper.updateWatchedStateForSeason(season.getId(), watchStatus);
        }
    }

    public class FilerSeries extends Filter {

        private final SeriesListAdapter adapter;
        final List<Series> seriesList;
        final List<Series> filteredSeriesList;

        FilerSeries(SeriesListAdapter adapter, List<Series> seriesList) {
            this.adapter = adapter;
            this.seriesList = seriesList;
            this.filteredSeriesList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredSeriesList.clear();
            final FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filteredSeriesList.addAll(seriesList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for (Series series : seriesList) {
                    if (series.getName().toLowerCase().contains(filterPattern)) {
                        filteredSeriesList.add(series);
                    }
                }
            }

            results.values = filteredSeriesList;
            results.count = filteredSeriesList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults results) {
            adapter.filteredDataSet.clear();
            //noinspection unchecked
            adapter.filteredDataSet.addAll((List<Series>) results.values);
            adapter.notifyDataSetChanged();
        }
    }

    public class UpdatedSeries {
        private Series prev;
        private Series passiveSeries;

        UpdatedSeries(Series prev, Series passiveSeries) {
            this.prev = prev;
            this.passiveSeries = passiveSeries;
        }

        Series getPrev() {
            return prev;
        }

        Series getPassiveSeries() {
            return passiveSeries;
        }
    }
}
