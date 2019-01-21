package de.cineaste.android.controllFlow.series

import android.content.Context
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import de.cineaste.android.R
import de.cineaste.android.adapter.series.SeriesListAdapter
import de.cineaste.android.controllFlow.BaseSnackBar
import de.cineaste.android.entity.series.Series

class SeriesSnackBarWatchList internal constructor(linearLayoutManager: LinearLayoutManager, view: View, private val adapter: SeriesListAdapter, private val context: Context) : BaseSnackBar(linearLayoutManager, view) {

    override fun getSnackBarLeftSwipe(position: Int) {
        val seriesToBeDeleted = adapter.getItem(position)
        adapter.removeItem(position)

        val currentSeason = seriesToBeDeleted.currentNumberOfSeason
        val currentEpisode = seriesToBeDeleted.currentNumberOfEpisode

        val mySnackbar = Snackbar.make(view,
                R.string.series_deleted, Snackbar.LENGTH_LONG)
        mySnackbar.setAction(R.string.undo) {
            //do nothing
        }
        mySnackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                when (event) {
                    Snackbar.Callback.DISMISS_EVENT_ACTION -> {
                        adapter.addDeletedItemToWatchListAgain(seriesToBeDeleted, position, currentSeason, currentEpisode)
                        val first = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                        if (first >= position) {
                            linearLayoutManager.scrollToPosition(position)
                        }
                    }
                }
            }

        })
        mySnackbar.show()
    }

    override fun getSnackBarRightSwipe(position: Int, message: Int) {
        val seriesToBeUpdated = adapter.getItem(position)

        showDialogIfNeeded(seriesToBeUpdated, position, message)
    }

    private fun showDialogIfNeeded(series: Series, position: Int, message: Int) {

        adapter.removeSeriesFromList(series)
        if (series.isInProduction) {
            val alertBuilder = AlertDialog.Builder(context)
            alertBuilder.setTitle(context.getString(R.string.seriesSeenHeadline, series.name))
            alertBuilder.setMessage(R.string.seriesStillInProduction)
            alertBuilder.setPositiveButton(R.string.ok) { _, _ -> updateSeriesAndCreateSnackbar(position, message, series) }
            alertBuilder.setNegativeButton(R.string.cancel) { _, _ -> adapter.addSeriesToList(series, position) }

            alertBuilder.create().show()
        } else {
            updateSeriesAndCreateSnackbar(position, message, series)
        }
    }

    //todo  reset to current season end episode after dismiss update current status
    private fun updateSeriesAndCreateSnackbar(position: Int, message: Int, seriesToBeUpdated: Series) {

        val currentSeason = seriesToBeUpdated.currentNumberOfSeason
        val currentEpisode = seriesToBeUpdated.currentNumberOfEpisode

        adapter.moveToHistory(seriesToBeUpdated)
        val mySnackbar = Snackbar.make(view,
                message, Snackbar.LENGTH_LONG)
        mySnackbar.setAction(R.string.undo) {
            //do nothing
        }
        mySnackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                when (event) {
                    Snackbar.Callback.DISMISS_EVENT_ACTION -> {
                        adapter.moveBackToWatchList(seriesToBeUpdated, position, currentSeason, currentEpisode)
                        val first = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                        if (first >= position) {
                            linearLayoutManager.scrollToPosition(position)
                        }
                    }
                }
            }
        })
        mySnackbar.show()
    }
}
