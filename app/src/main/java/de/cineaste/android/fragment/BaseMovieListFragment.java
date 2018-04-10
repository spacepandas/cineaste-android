package de.cineaste.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import de.cineaste.android.R;
import de.cineaste.android.activity.MovieDetailActivity;
import de.cineaste.android.activity.MovieSearchActivity;
import de.cineaste.android.adapter.movie.MovieListAdapter;
import de.cineaste.android.controllFlow.movie.HistoryListMovieTouchHelperCallback;
import de.cineaste.android.controllFlow.movie.WatchlistMovieTouchHelperCallback;
import de.cineaste.android.database.dao.BaseDao;

public class BaseMovieListFragment extends BaseListFragment {

    private MovieListAdapter movieListAdapter;

    @Override
    public void updateAdapter() {
        movieListAdapter.updateDataSet();
    }

    @Override
    protected int getSubtitle() {
        return R.string.movies;
    }

    @Override
    protected void initFab(final Activity activity, View watchlistView) {
        FloatingActionButton fab = watchlistView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MovieSearchActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    protected void initRecyclerView() {
        customRecyclerView.setLayoutManager(layoutManager);
        customRecyclerView.setAdapter(movieListAdapter);
    }

    @Override
    protected void initAdapter(Activity activity) {
        movieListAdapter = new MovieListAdapter(this, activity, this, watchState);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_movielist;
    }


    @Override
    protected void filterOnQueryTextChange(String newText) {
        ((MovieListAdapter) customRecyclerView.getAdapter()).getFilter().filter(newText);
    }

    @Override
    protected void reorderEntries(FilterType filterType) {
        switch (filterType) {
            case ALPHABETICAL:
                movieListAdapter.orderAlphabetical();
                break;
            case RELEASE_DATE:
                movieListAdapter.orderByReleaseDate();
                break;
            case RUNTIME:
                movieListAdapter.orderByRuntime();
                break;
        }

        movieListAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getDataSetSize() {
        return movieListAdapter.getDataSetSize();
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
        Intent intent = new Intent(activity, MovieDetailActivity.class);
        intent.putExtra(BaseDao.MovieEntry.ID, itemId);
        intent.putExtra(getString(R.string.state), state);
        return intent;
    }

    @Override
    protected ItemTouchHelper.Callback getCorrectCallBack() {
        if (watchState == WatchState.WATCH_STATE) {
            return new WatchlistMovieTouchHelperCallback(layoutManager, movieListAdapter, customRecyclerView, getResources());
        } else {
            return new HistoryListMovieTouchHelperCallback(layoutManager, movieListAdapter, customRecyclerView, getResources());
        }
    }
}
