package de.cineaste.android.adapter.series;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.viewholder.series.SeriesSearchViewHolder;

public class SeriesSearchQueryAdapter extends RecyclerView.Adapter<SeriesSearchViewHolder> {
    private final List<Series> dataSet = new ArrayList<>();
    private final ItemClickListener listener;
    private final OnSeriesStateChange seriesStateChange;

    public interface OnSeriesStateChange {
        void onSeriesStateChangeListener(Series series, int viewId, int index);
    }

    public SeriesSearchQueryAdapter(ItemClickListener listener, OnSeriesStateChange seriesStateChange) {
        this.listener = listener;
        this.seriesStateChange = seriesStateChange;
    }

    public void addSeries(List<Series> series) {
        dataSet.clear();
        dataSet.addAll(series);
        notifyDataSetChanged();
    }

    public void addSerie(Series series, int index) {
        dataSet.add(index, series);
    }

    public void removeSerie(int index) {
        dataSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public SeriesSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_series_search, parent, false);
        return new SeriesSearchViewHolder(v, listener, parent.getContext(), seriesStateChange);
    }

    @Override
    public void onBindViewHolder(SeriesSearchViewHolder holder, int position) {
        holder.assignData(dataSet.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
