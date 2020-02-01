package de.cineaste.android.adapter.series

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.cineaste.android.R
import de.cineaste.android.entity.series.Series
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.viewholder.series.SeriesSearchViewHolder

class SeriesSearchQueryAdapter(
    private val listener: ItemClickListener
) : RecyclerView.Adapter<SeriesSearchViewHolder>() {
    private val dataSet = ArrayList<Series>()

    fun addSeries(series: List<Series>) {
        dataSet.clear()
        dataSet.addAll(series)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesSearchViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_search, parent, false)
        return SeriesSearchViewHolder(view, listener, parent.context)
    }

    override fun onBindViewHolder(holder: SeriesSearchViewHolder, position: Int) {
        holder.assignData(dataSet[position], position)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}
