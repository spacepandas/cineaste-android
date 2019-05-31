package de.cineaste.android.controllFlow.movie

import android.content.res.Resources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import de.cineaste.android.R
import de.cineaste.android.adapter.movie.MovieListAdapter
import de.cineaste.android.controllFlow.BaseSnackBar

class HistoryListMovieTouchHelperCallback(
    linearLayoutManager: LinearLayoutManager,
    movieListAdapter: MovieListAdapter,
    recyclerView: RecyclerView,
    resources: Resources
) : BaseMovieTouchHelperCallback(linearLayoutManager, movieListAdapter, recyclerView, resources) {

    override val icon: Int
        get() = R.drawable.ic_add_to_watchlist_white

    override val snackBar: BaseSnackBar
        get() = MovieSnackBarHistory(linearLayoutManager, movieListAdapter, recyclerView)

    override val rightSwipeMessage: Int
        get() = R.string.not_seen
}
