package de.cineaste.android.controllFlow.movie

import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import de.cineaste.android.R
import de.cineaste.android.adapter.movie.MovieListAdapter
import de.cineaste.android.controllFlow.BaseSnackBar

class MovieSnackBarWatchList internal constructor(linearLayoutManager: LinearLayoutManager, private val adapter: MovieListAdapter, view: View) : BaseSnackBar(linearLayoutManager, view) {

    override fun getSnackBarLeftSwipe(position: Int) {
        val movieToBeDeleted = adapter.getItem(position)
        adapter.removeItem(position)

        val mySnackbar = Snackbar.make(view,
                R.string.movie_deleted, Snackbar.LENGTH_LONG)
        mySnackbar.setAction(R.string.undo) {
            //do nothing
        }
        mySnackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                when (event) {
                    Snackbar.Callback.DISMISS_EVENT_ACTION -> {
                        adapter.restoreDeletedItem(movieToBeDeleted, position)
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
        val movieToBeUpdated = adapter.getItem(position)
        adapter.toggleItemOnList(movieToBeUpdated)

        val mySnackbar = Snackbar.make(view,
                message, Snackbar.LENGTH_LONG)
        mySnackbar.setAction(R.string.undo) {
            //do nothing
        }
        mySnackbar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                when (event) {
                    Snackbar.Callback.DISMISS_EVENT_ACTION -> {
                        adapter.restoreToggleItemOnList(movieToBeUpdated, position)
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
