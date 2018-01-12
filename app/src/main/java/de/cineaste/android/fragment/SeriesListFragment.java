package de.cineaste.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import de.cineaste.android.R;
import de.cineaste.android.activity.SeriesDetailActivity;
import de.cineaste.android.activity.SeriesSearchActivity;
import de.cineaste.android.adapter.series.SeriesListAdapter;
import de.cineaste.android.controllFlow.series.HistoryListSeriesTouchHelperCallback;
import de.cineaste.android.controllFlow.series.WatchlistSeriesTouchHelperCallback;
import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.entity.series.Series;

public class SeriesListFragment extends BaseListFragment implements SeriesListAdapter.OnEpisodeWatchedClickListener {

    private SeriesListAdapter seriesListAdapter;
    @Override
    public void onEpisodeWatchedClick(Series series, int position) {
        SeriesDbHelper.getInstance(getActivity()).episodeWatched(series);
        seriesListAdapter.updateSeries(series, position);
    }

    @Override
    public void updateAdapter() {
        seriesListAdapter.updateDataSet();
    }

    @Override
    protected int getSubtitle() {
        return R.string.series;
    }

    @Override
    protected void initFab(final Activity activity, View watchlistView) {
        FloatingActionButton fab = watchlistView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SeriesSearchActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    protected void initRecyclerView() {
        customRecyclerView.setLayoutManager(layoutManager);
        customRecyclerView.setAdapter(seriesListAdapter);
    }

    @Override
    protected void initAdapter(Activity activity) {
        seriesListAdapter = new SeriesListAdapter(this, activity, this, watchState, this);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_series_list;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem movieNight = menu.findItem(R.id.startMovieNight);
        movieNight.setVisible(false);
    }

    @Override
    protected void filterOnQueryTextChange(String newText) {
        ((SeriesListAdapter) customRecyclerView.getAdapter()).getFilter().filter(newText);
    }

    @Override
    protected void reorderEntries(FilterType filterType) {
        switch (filterType) {
            case ALPHABETICAL:
                seriesListAdapter.orderAlphabetical();
                break;
            case RELEASE_DATE:
                seriesListAdapter.orderByReleaseDate();
                break;
        }

        seriesListAdapter.notifyDataSetChanged();
    }


    @Override
    protected int getDataSetSize() {
        return seriesListAdapter.getDataSetSize();
    }

    @Override
    protected int getEmptyListMessageByState() {
        if (watchState == WatchState.WATCH_STATE) {
            return R.string.noMoviesOnWatchList;
        } else {
            return R.string.noMoviesOnWatchedList;
        }
    }

    @Override
    @NonNull
    protected Intent createIntent(long itemId, int state, Activity activity) {
        Intent intent = new Intent(activity, SeriesDetailActivity.class);
        intent.putExtra(BaseDao.SeriesEntry._ID, itemId);
        intent.putExtra(getString(R.string.state), state);
        return intent;
    }

    @Override
    protected ItemTouchHelper.Callback getCorrectCallBack() {
        if (watchState == WatchState.WATCH_STATE) {
            return new WatchlistSeriesTouchHelperCallback(getResources(), layoutManager, customRecyclerView, seriesListAdapter);
        } else {
            return new HistoryListSeriesTouchHelperCallback(getResources(), layoutManager, customRecyclerView, seriesListAdapter);
        }
    }
}
