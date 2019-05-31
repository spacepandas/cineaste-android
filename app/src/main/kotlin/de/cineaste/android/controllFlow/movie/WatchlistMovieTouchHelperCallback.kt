package de.cineaste.android.controllFlow.movie

import android.content.res.Resources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import de.cineaste.android.R
import de.cineaste.android.adapter.movie.MovieListAdapter
import de.cineaste.android.controllFlow.BaseSnackBar

class WatchlistMovieTouchHelperCallback(
    linearLayoutManager: LinearLayoutManager,
    movieListAdapter: MovieListAdapter,
    recyclerView: RecyclerView,
    resources: Resources
) : BaseMovieTouchHelperCallback(linearLayoutManager, movieListAdapter, recyclerView, resources) {

    override val snackBar: BaseSnackBar
        get() = MovieSnackBarWatchList(linearLayoutManager, movieListAdapter, recyclerView)

    override val icon: Int
        get() = R.drawable.ic_add_to_history_white

    override val rightSwipeMessage: Int
        get() = R.string.movie_seen
}
