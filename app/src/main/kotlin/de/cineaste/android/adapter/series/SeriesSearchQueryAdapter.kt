package de.cineaste.android.adapter.series

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.cineaste.android.R
import de.cineaste.android.entity.series.Series
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.viewholder.series.SeriesSearchViewHolder
import java.util.*

class SeriesSearchQueryAdapter(private val listener: ItemClickListener, private val seriesStateChange: OnSeriesStateChange) : RecyclerView.Adapter<SeriesSearchViewHolder>() {
    private val dataSet = ArrayList<Series>()

    interface OnSeriesStateChange {
        fun onSeriesStateChangeListener(series: Series, viewId: Int, index: Int)
    }

    fun addSeries(series: List<Series>) {
        dataSet.clear()
        dataSet.addAll(series)
        notifyDataSetChanged()
    }

    fun addSerie(series: Series, index: Int) {
        dataSet.add(index, series)
    }

    fun removeSerie(index: Int) {
        dataSet.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesSearchViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.card_series_search, parent, false)
        return SeriesSearchViewHolder(view, listener, parent.context, seriesStateChange)
    }

    override fun onBindViewHolder(holder: SeriesSearchViewHolder, position: Int) {
        holder.assignData(dataSet[position], position)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}
