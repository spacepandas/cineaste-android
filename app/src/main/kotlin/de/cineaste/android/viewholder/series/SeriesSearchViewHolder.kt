package de.cineaste.android.viewholder.series

import android.content.Context
import android.view.View
import android.widget.TextView
import de.cineaste.android.R
import de.cineaste.android.entity.series.Series
import de.cineaste.android.listener.ItemClickListener

class SeriesSearchViewHolder(
    itemView: View,
    listener: ItemClickListener,
    context: Context
) : AbstractSeriesViewHolder(itemView, listener, context) {

    private val releaseDate: TextView = itemView.findViewById(R.id.releaseDate)

    override fun assignData(series: Series, position: Int) {
        setBaseInformation(series)
        val seriesReleaseDate = series.releaseDate
        if (seriesReleaseDate != null) {
            releaseDate.text = convertDate(seriesReleaseDate)
        } else {
            releaseDate.text = resources.getString(R.string.coming_soon)
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
