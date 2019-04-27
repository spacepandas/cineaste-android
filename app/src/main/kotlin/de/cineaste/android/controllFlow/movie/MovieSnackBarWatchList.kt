package de.cineaste.android.controllFlow.movie

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import de.cineaste.android.R
import de.cineaste.android.adapter.movie.MovieListAdapter
import de.cineaste.android.controllFlow.BaseSnackBar

class MovieSnackBarWatchList internal constructor(
    linearLayoutManager: LinearLayoutManager,
    private val adapter: MovieListAdapter,
    view: View
) : BaseSnackBar(linearLayoutManager, view) {

    override fun getSnackBarLeftSwipe(position: Int) {
        val movieToBeDeleted = adapter.getItem(position)
        adapter.removeItem(position)

        val mySnackBar = Snackbar.make(
            view,
            R.string.movie_deleted, Snackbar.LENGTH_LONG
        )
        mySnackBar.setAction(R.string.undo) {
            // do nothing
        }
        mySnackBar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    adapter.restoreDeletedItem(movieToBeDeleted, position)
                    val first = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    if (first >= position) {
                        linearLayoutManager.scrollToPosition(position)
                    }
                }
            }
        })
        mySnackBar.show()
    }

    override fun getSnackBarRightSwipe(position: Int, message: Int) {
        val movieToBeUpdated = adapter.getItem(position)
        adapter.toggleItemOnList(movieToBeUpdated)

        val mySnackBar = Snackbar.make(
            view,
            message, Snackbar.LENGTH_LONG
        )
        mySnackBar.setAction(R.string.undo) {
            // do nothing
        }
        mySnackBar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                if (event == Snackbar.Callback.DISMISS_EVENT_ACTION) {
                    adapter.restoreToggleItemOnList(movieToBeUpdated, position)
                    val first = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                    if (first >= position) {
                        linearLayoutManager.scrollToPosition(position)
                    }
                }
            }
        })
        mySnackBar.show()
    }
}
