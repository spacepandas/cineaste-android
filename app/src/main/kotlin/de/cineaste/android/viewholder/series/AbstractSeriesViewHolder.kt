package de.cineaste.android.viewholder.series

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView

import de.cineaste.android.R
import de.cineaste.android.entity.series.Series
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.viewholder.BaseViewHolder

abstract class AbstractSeriesViewHolder(
    itemView: View,
    listener: ItemClickListener,
    context: Context
) :
    BaseViewHolder(itemView, listener, context) {

    private val vote: TextView = itemView.findViewById(R.id.vote)

    abstract fun assignData(series: Series, position: Int)

    fun setBaseInformation(series: Series) {
        title.text = series.name
        vote.text = resources.getString(R.string.vote, series.voteAverage.toString())

        var posterName = series.posterPath
        if (TextUtils.isEmpty(posterName)) {
            posterName = series.posterPath
        }
        setPoster(posterName)
    }
}
