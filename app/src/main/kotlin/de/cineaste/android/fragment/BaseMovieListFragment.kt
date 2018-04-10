package de.cineaste.android.fragment

import android.app.Activity
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View

import de.cineaste.android.R
import de.cineaste.android.activity.MovieDetailActivity
import de.cineaste.android.activity.MovieSearchActivity
import de.cineaste.android.adapter.movie.MovieListAdapter
import de.cineaste.android.controllFlow.movie.HistoryListMovieTouchHelperCallback
import de.cineaste.android.controllFlow.movie.WatchlistMovieTouchHelperCallback
import de.cineaste.android.database.dao.BaseDao

class BaseMovieListFragment : BaseListFragment() {

    private var movieListAdapter: MovieListAdapter? = null

    override val subtitle: Int
        get() = R.string.movies

    override val layout: Int
        get() = R.layout.fragment_movielist

    override val dataSetSize: Int
        get() = movieListAdapter!!.dataSetSize

    override val emptyListMessageByState: Int
        get() = if (watchState == WatchState.WATCH_STATE) {
            R.string.noMoviesOnWatchList
        } else {
            R.string.noMoviesOnWatchedList
        }

    override val correctCallBack: ItemTouchHelper.Callback
        get() = if (watchState == WatchState.WATCH_STATE) {
            WatchlistMovieTouchHelperCallback(layoutManager, movieListAdapter!!, customRecyclerView, resources)
        } else {
            HistoryListMovieTouchHelperCallback(layoutManager, movieListAdapter!!, customRecyclerView, resources)
        }

    override fun updateAdapter() {
        movieListAdapter!!.updateDataSet()
    }

    override fun initFab(activity: Activity, watchlistView: View) {
        val fab = watchlistView.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(getActivity(), MovieSearchActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun initRecyclerView() {
        customRecyclerView.layoutManager = layoutManager
        customRecyclerView.adapter = movieListAdapter
    }

    override fun initAdapter(activity: Activity) {
        movieListAdapter = MovieListAdapter(this, activity, this, watchState)
    }


    override fun filterOnQueryTextChange(newText: String) {
        (customRecyclerView.adapter as MovieListAdapter).filter.filter(newText)
    }

    override fun reorderEntries(filterType: BaseListFragment.FilterType) {
        when (filterType) {
            BaseListFragment.FilterType.ALPHABETICAL -> movieListAdapter!!.orderAlphabetical()
            BaseListFragment.FilterType.RELEASE_DATE -> movieListAdapter!!.orderByReleaseDate()
            BaseListFragment.FilterType.RUNTIME -> movieListAdapter!!.orderByRuntime()
        }

        movieListAdapter!!.notifyDataSetChanged()
    }

    override fun createIntent(itemId: Long, state: Int, activity: Activity): Intent {
        val intent = Intent(activity, MovieDetailActivity::class.java)
        intent.putExtra(BaseDao.MovieEntry.ID, itemId)
        intent.putExtra(getString(R.string.state), state)
        return intent
    }
}
