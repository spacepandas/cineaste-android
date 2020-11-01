package de.cineaste.android.fragment

import android.app.Activity
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
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

    private lateinit var seriesListAdapter: SeriesListAdapter

    override val subtitle: String
        get() = resources.getQuantityString(R.plurals.seriesTitle, dataSetSize, dataSetSize)

    override val layout: Int
        get() = R.layout.fragment_series_list

    override val dataSetSize: Int
        get() = seriesListAdapter.dataSetSize

    override val emptyListMessageByState: Int
        get() = if (watchState == WatchState.WATCH_STATE) {
            R.string.noSeriesOnWatchList
        } else {
            R.string.noSeriesOnWatchedList
        }

    override val correctCallBack: ItemTouchHelper.Callback
        get() = if (watchState == WatchState.WATCH_STATE) {
            WatchlistSeriesTouchHelperCallback(
                resources,
                layoutManager,
                customRecyclerView,
                seriesListAdapter,
                activity!!
            )
        } else {
            HistoryListSeriesTouchHelperCallback(
                resources,
                layoutManager,
                customRecyclerView,
                seriesListAdapter
            )
        }

    override fun onEpisodeWatchedClick(series: Series, position: Int) {
        val activity = activity
        activity?.let {
            SeriesDbHelper.getInstance(activity).episodeWatched(series)
            seriesListAdapter.updateSeries(series, position)
        }
    }

    override fun updateAdapter() {
        seriesListAdapter.updateDataSet()
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

        val divider = ContextCompat.getDrawable(customRecyclerView.context, R.drawable.divider)
        val itemDecor = DividerItemDecoration(
            customRecyclerView.context,
            layoutManager.orientation
        )
        divider?.let {
            itemDecor.setDrawable(it)
        }
        customRecyclerView.addItemDecoration(itemDecor)
    }

    override fun initAdapter(activity: Activity) {
        seriesListAdapter = SeriesListAdapter(this, activity, this, watchState, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        val sortByRuntime = menu.findItem(R.id.filterRunTime)
        sortByRuntime.isVisible = false
    }

    override fun filterOnQueryTextChange(newText: String) {
        (customRecyclerView.adapter as SeriesListAdapter).filter.filter(newText)
    }

    override fun reorderEntries(filterType: FilterType) {
        when (filterType) {
            FilterType.ALPHABETICAL -> seriesListAdapter.orderAlphabetical()
            FilterType.RELEASE_DATE -> seriesListAdapter.orderByReleaseDate()
            else -> {
            }
        }

        seriesListAdapter.notifyDataSetChanged()
    }

    override fun createIntent(itemId: Long, state: Int, activity: Activity): Intent {
        val intent = Intent(activity, SeriesDetailActivity::class.java)
        intent.putExtra(BaseDao.SeriesEntry.ID, itemId)
        intent.putExtra(getString(R.string.state), state)
        return intent
    }
}