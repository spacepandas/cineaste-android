package de.cineaste.android.adapter.series

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Filter
import de.cineaste.android.R
import de.cineaste.android.adapter.BaseListAdapter
import de.cineaste.android.database.dbHelper.SeriesDbHelper
import de.cineaste.android.entity.series.Series
import de.cineaste.android.fragment.WatchState
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.viewholder.series.SeriesViewHolder
import java.util.concurrent.LinkedBlockingQueue
import java.util.Collections
import java.util.LinkedList

class SeriesListAdapter(
    displayMessage: DisplayMessage,
    context: Context,
    listener: ItemClickListener,
    state: WatchState,
    private val onEpisodeWatchedClickListener: OnEpisodeWatchedClickListener
) : BaseListAdapter(context, displayMessage, listener, state) {

    private val db: SeriesDbHelper
    private var dataSet: MutableList<Series> = mutableListOf()
    private var filteredDataSet: MutableList<Series> = mutableListOf()

    private val updatedSeries = LinkedBlockingQueue<UpdatedSeries>()

    override val internalFilter: Filter
        get() = FilerSeries(this, dataSet)

    val dataSetSize: Int
        get() = dataSet.size

    override val layout: Int
        get() = R.layout.card_series

    init {
        this.dataSet.clear()
        this.db = SeriesDbHelper.getInstance(context)
        this.dataSet.addAll(db.getSeriesByWatchedState(state))
        this.filteredDataSet = LinkedList(dataSet)
    }

    interface OnEpisodeWatchedClickListener {
        fun onEpisodeWatchedClick(series: Series, position: Int)
    }

    fun removeItem(position: Int) {
        val series = filteredDataSet[position]
        db.delete(series.id)
        removeSeriesFromList(series)
    }

    fun getItem(position: Int): Series {
        return filteredDataSet[position]
    }

    fun updateSeries(givenSeries: Series, pos: Int) {
        db.getSeriesById(givenSeries.id)?.let { series ->
            dataSet.removeAt(pos)
            filteredDataSet.removeAt(pos)
            if (state == WatchState.WATCH_STATE && !series.isWatched) {
                dataSet.add(pos, series)
                filteredDataSet.add(pos, series)
                notifyItemChanged(pos)
            } else {
                notifyItemRemoved(pos)
            }
        }
    }

    fun addDeletedItemToHistoryAgain(
        series: Series,
        position: Int,
        prevSeason: Int,
        prevEpisode: Int
    ) {
        db.addToHistory(series)
        db.moveBackToHistory(series, prevSeason, prevEpisode)
        addSeriesToList(series, position)
    }

    fun addDeletedItemToWatchListAgain(
        series: Series,
        position: Int,
        prevSeason: Int,
        prevEpisode: Int
    ) {
        db.addToWatchList(series)
        db.moveBackToWatchList(series, prevSeason, prevEpisode)
        addSeriesToList(series, position)
    }

    fun moveToWatchList(series: Series) {
        removeSeriesFromList(series)

        db.moveToWatchList(series)
    }

    fun moveBackToHistory(series: Series, position: Int, prevSeason: Int, prevEpisode: Int) {
        db.moveBackToHistory(series, prevSeason, prevEpisode)
        addSeriesToList(series, position)
    }

    fun moveToHistory(series: Series) {
        db.moveToHistory(series)
    }

    fun moveBackToWatchList(series: Series, position: Int, prevSeason: Int, prevEpisode: Int) {
        db.moveBackToWatchList(series, prevSeason, prevEpisode)
        addSeriesToList(series, position)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        updateSeriesPositionsAndAddToQueue(fromPosition, toPosition)

        Collections.swap(filteredDataSet, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun updateSeriesPositionsAndAddToQueue(fromPosition: Int, toPosition: Int) {
        val passiveMovedSeries = filteredDataSet.removeAt(toPosition)
        passiveMovedSeries.listPosition = fromPosition
        filteredDataSet.add(toPosition, passiveMovedSeries)

        val prev = filteredDataSet.removeAt(fromPosition)
        prev.listPosition = toPosition
        filteredDataSet.add(fromPosition, prev)

        updatedSeries.add(UpdatedSeries(prev, passiveMovedSeries))
    }

    fun orderAlphabetical() {
        val series = db.reorderAlphabetical(state)
        dataSet = series as MutableList<Series>
        filteredDataSet = series
    }

    fun orderByReleaseDate() {
        val series = db.reorderByReleaseDate(state)
        dataSet = series as MutableList<Series>
        filteredDataSet = series
    }

    fun updatePositionsInDb() {
        while (updatedSeries.iterator().hasNext()) {
            val series = updatedSeries.poll()

            db.updatePosition(series.prev)
            db.updatePosition(series.passiveSeries)
        }
    }

    override fun getItemCount(): Int {
        return filteredDataSet.size
    }

    override fun createViewHolder(v: View): RecyclerView.ViewHolder {
        return SeriesViewHolder(v, listener, context, state, onEpisodeWatchedClickListener)
    }

    override fun assignDataToViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SeriesViewHolder).assignData(filteredDataSet[position], position)
    }

    fun removeSeriesFromList(series: Series) {
        notifyItemRemoved(filteredDataSet.indexOf(series))

        dataSet.remove(series)
        filteredDataSet.remove(series)
        displayMessage.showMessageIfEmptyList()
    }

    fun addSeriesToList(series: Series, position: Int) {
        filteredDataSet.add(position, series)
        dataSet.add(position, series)
        notifyItemInserted(position)
    }

    fun updateDataSet() {
        this.dataSet = db.getSeriesByWatchedState(state) as MutableList<Series>
        this.filteredDataSet = LinkedList(dataSet)
        displayMessage.showMessageIfEmptyList()
        notifyDataSetChanged()
    }

    inner class FilerSeries internal constructor(
        private val adapter: SeriesListAdapter,
        private val seriesList: List<Series>
    ) : Filter() {
        private val filteredSeriesList: MutableList<Series>

        init {
            this.filteredSeriesList = ArrayList()
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            filteredSeriesList.clear()

            if (constraint.isNullOrEmpty()) {
                filteredSeriesList.addAll(seriesList)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }

                for (series in seriesList) {
                    if (series.name.toLowerCase().contains(filterPattern)) {
                        filteredSeriesList.add(series)
                    }
                }
            }

            val results = FilterResults()
            results.values = filteredSeriesList
            results.count = filteredSeriesList.size

            return results
        }

        override fun publishResults(charSequence: CharSequence, results: FilterResults) {
            adapter.filteredDataSet.clear()

            adapter.filteredDataSet.addAll(results.values as List<Series>)
            adapter.notifyDataSetChanged()
        }
    }

    inner class UpdatedSeries internal constructor(
        internal val prev: Series,
        internal val passiveSeries: Series
    )
}
