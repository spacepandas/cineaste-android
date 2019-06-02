package de.cineaste.android.viewholder.series

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView

import de.cineaste.android.R
import de.cineaste.android.adapter.series.SeriesListAdapter
import de.cineaste.android.entity.series.Series
import de.cineaste.android.fragment.WatchState
import de.cineaste.android.listener.ItemClickListener

class SeriesViewHolder(
    itemView: View,
    listener: ItemClickListener,
    context: Context,
    state: WatchState,
    private val onEpisodeWatchedClickListener: SeriesListAdapter.OnEpisodeWatchedClickListener
) : AbstractSeriesViewHolder(itemView, listener, context) {

    private val seasons: TextView = itemView.findViewById(R.id.season)
    private val currentStatus: TextView = itemView.findViewById(R.id.current)
    private val episodeSeen: ImageButton = itemView.findViewById(R.id.episode_seen)

    init {
        if (state == WatchState.WATCHED_STATE) {
            episodeSeen.visibility = View.GONE
        }
    }

    override fun assignData(series: Series, position: Int) {
        setBaseInformation(series)

        seasons.text = resources.getString(R.string.seasons, series.numberOfSeasons.toString())
        currentStatus.text = resources.getString(
            R.string.currentStatus,
            series.currentNumberOfSeason.toString(),
            series.currentNumberOfEpisode.toString()
        )

        episodeSeen.setOnClickListener {
            onEpisodeWatchedClickListener.onEpisodeWatchedClick(
                series,
                position
            )
        }

        listener?.let {
            view.setOnClickListener { view ->
                listener.onItemClickListener(
                    series.id,
                    arrayOf(view, poster, title)
                )
            }
        }
    }

    fun onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY)
    }

    fun onItemClear() {
        itemView.setBackgroundColor(Color.WHITE)
    }
}
