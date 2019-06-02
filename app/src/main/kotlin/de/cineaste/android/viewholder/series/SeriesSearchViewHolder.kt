package de.cineaste.android.viewholder.series

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView

import de.cineaste.android.R
import de.cineaste.android.adapter.series.SeriesSearchQueryAdapter
import de.cineaste.android.entity.series.Series
import de.cineaste.android.listener.ItemClickListener

class SeriesSearchViewHolder(
    itemView: View,
    listener: ItemClickListener,
    context: Context,
    private val seriesStateChange: SeriesSearchQueryAdapter.OnSeriesStateChange
) : AbstractSeriesViewHolder(itemView, listener, context) {

    private val releaseDate: TextView = itemView.findViewById(R.id.releaseDate)
    private val addToWatchlistButton: Button = itemView.findViewById(R.id.to_watchlist_button)
    private val watchedButton: Button = itemView.findViewById(R.id.history_button)

    override fun assignData(series: Series, position: Int) {
        setBaseInformation(series)
        val seriesReleaseDate = series.releaseDate
        if (seriesReleaseDate != null) {
            releaseDate.text = convertDate(seriesReleaseDate)
            releaseDate.visibility = View.VISIBLE
        } else {
            releaseDate.visibility = View.GONE
        }

        addToWatchlistButton.setOnClickListener { v ->
            val index = this@SeriesSearchViewHolder.adapterPosition
            seriesStateChange.onSeriesStateChangeListener(series, v.id, index)
        }

        watchedButton.setOnClickListener { v ->
            val index = this@SeriesSearchViewHolder.adapterPosition
            seriesStateChange.onSeriesStateChangeListener(series, v.id, index)
        }

        listener?.let {
            view.setOnClickListener { view ->
                listener.onItemClickListener(
                    series.id,
                    arrayOf(view, poster)
                )
            }
        }
    }
}
