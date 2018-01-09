package de.cineaste.android.adapter.series;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import de.cineaste.android.R;
import de.cineaste.android.database.SeriesDbHelper;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.fragment.WatchState;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.viewholder.series.SeriesViewHolder;


public class SeriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final Context context;
    private final SeriesDbHelper db;
    private final ItemClickListener listener;
    private final WatchState state;
    private final DisplayMessage displayMessage;
    private List<Series> dataSet = new ArrayList<>();
    private List<Series> filteredDataSet;
    private OnEpisodeWatchedClickListener onEpisodeWatchedClickListener;

    private final Queue<UpdatedSeries> updatedSeries = new LinkedBlockingQueue<>();

    public SeriesListAdapter(DisplayMessage displayMessage, Context context, ItemClickListener listener, WatchState state, OnEpisodeWatchedClickListener onEpisodeWatchedClickListener) {
        this.context = context;
        this.listener = listener;
        this.state = state;
        this.displayMessage = displayMessage;
        this.onEpisodeWatchedClickListener = onEpisodeWatchedClickListener;
        this.dataSet.clear();
        this.db = SeriesDbHelper.getInstance(context);
        this.dataSet.addAll(db.readSeriesByWatchStatus(state));
        this.filteredDataSet = new LinkedList<>(dataSet);
    }

    public interface DisplayMessage {
        void showMessageIfEmptyList();
    }

    public interface OnEpisodeWatchedClickListener {
        void onEpisodeWatchedClick(Series series, int position);
    }

    @Override
    public Filter getFilter() {
        return new FilerSeries(this, dataSet);
    }

    public void removeItem(int position) {
        Series series = filteredDataSet.get(position);
        db.delete(series.getId());
        removeSeries(series);
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
        filteredDataSet.add(position, item);
        dataSet.add(position, item);
        notifyItemInserted(position);
    }

    public void toggleItemOnList(Series item) {
        item.setWatched(!item.isWatched());
        db.createOrUpdate(item);
        removeSeries(item);
    }
    public void restoreToggleItemOnList(Series item, int position) {
        item.setWatched(!item.isWatched());
        db.createOrUpdate(item);
        filteredDataSet.add(position, item);
        dataSet.add(position, item);
        notifyItemInserted(position);
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
    public SeriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_series, parent, false);

        return new SeriesViewHolder(v, listener, context, state, onEpisodeWatchedClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SeriesViewHolder) holder).assignData(filteredDataSet.get(position), position);
    }

    public void removeSeries(Series series) {
        notifyItemRemoved(filteredDataSet.indexOf(series));

        dataSet.remove(series);
        filteredDataSet.remove(series);
        displayMessage.showMessageIfEmptyList();
    }

    public void updateDataSet() {
        this.dataSet = db.readSeriesByWatchStatus(state);
        this.filteredDataSet = new LinkedList<>(dataSet);
        displayMessage.showMessageIfEmptyList();
        notifyDataSetChanged();
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

        public Series getPrev() {
            return prev;
        }

        public Series getPassiveSeries() {
            return passiveSeries;
        }
    }
}
