package de.cineaste.android.fragment

import android.app.Activity
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuInflater
import android.view.View

import de.cineaste.android.R
import de.cineaste.android.activity.SeriesDetailActivity
import de.cineaste.android.activity.SeriesSearchActivity
import de.cineaste.android.adapter.series.SeriesListAdapter
import de.cineaste.android.controllFlow.series.HistoryListSeriesTouchHelperCallback
import de.cineaste.android.controllFlow.series.WatchlistSeriesTouchHelperCallback
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.dbHelper.SeriesDbHelper
import de.cineaste.android.entity.series.Series

class SeriesListFragment : BaseListFragment(), SeriesListAdapter.OnEpisodeWatchedClickListener {

    private var seriesListAdapter: SeriesListAdapter? = null

    override val subtitle: Int
        get() = R.string.series

    override val layout: Int
        get() = R.layout.fragment_series_list


    override val dataSetSize: Int
        get() = seriesListAdapter!!.dataSetSize

    override val emptyListMessageByState: Int
        get() = if (watchState == WatchState.WATCH_STATE) {
            R.string.noMoviesOnWatchList
        } else {
            R.string.noMoviesOnWatchedList
        }

    override val correctCallBack: ItemTouchHelper.Callback
        get() = if (watchState == WatchState.WATCH_STATE) {
            WatchlistSeriesTouchHelperCallback(resources, layoutManager, customRecyclerView, seriesListAdapter!!, activity!!)
        } else {
            HistoryListSeriesTouchHelperCallback(resources, layoutManager, customRecyclerView, seriesListAdapter!!)
        }

    override fun onEpisodeWatchedClick(series: Series, position: Int) {
        SeriesDbHelper.getInstance(activity!!).episodeWatched(series)
        seriesListAdapter!!.updateSeries(series, position)
    }

    override fun updateAdapter() {
        seriesListAdapter!!.updateDataSet()
    }

    override fun initFab(activity: Activity, view: View) {
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(getActivity(), SeriesSearchActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun initRecyclerView() {
        customRecyclerView.layoutManager = layoutManager
        customRecyclerView.adapter = seriesListAdapter
    }

    override fun initAdapter(activity: Activity) {
        seriesListAdapter = SeriesListAdapter(this, activity, this, watchState, this)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        val movieNight = menu!!.findItem(R.id.startMovieNight)
        movieNight.isVisible = false

        val sortByRuntime = menu.findItem(R.id.filterRunTime)
        sortByRuntime.isVisible = false
    }

    override fun filterOnQueryTextChange(newText: String) {
        (customRecyclerView.adapter as SeriesListAdapter).filter.filter(newText)
    }

    override fun reorderEntries(filterType: BaseListFragment.FilterType) {
        when (filterType) {
            BaseListFragment.FilterType.ALPHABETICAL -> seriesListAdapter!!.orderAlphabetical()
            BaseListFragment.FilterType.RELEASE_DATE -> seriesListAdapter!!.orderByReleaseDate()
            else -> { }
        }

        seriesListAdapter!!.notifyDataSetChanged()
    }

    override fun createIntent(itemId: Long, state: Int, activity: Activity): Intent {
        val intent = Intent(activity, SeriesDetailActivity::class.java)
        intent.putExtra(BaseDao.SeriesEntry.ID, itemId)
        intent.putExtra(getString(R.string.state), state)
        return intent
    }
}
