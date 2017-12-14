package de.cineaste.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.activity.MovieNightActivity;
import de.cineaste.android.activity.SeriesSearchActivity;
import de.cineaste.android.adapter.SeriesListAdapter;
import de.cineaste.android.database.SeriesDbHelper;
import de.cineaste.android.database.UserDbHelper;
import de.cineaste.android.entity.Series;
import de.cineaste.android.util.CustomRecyclerView;

public class SeriesListFragment extends Fragment implements SeriesListAdapter.DisplayMessage, SeriesListAdapter.OnEpisodeWatchedClickListener {

    private WatchState watchState;
    private CustomRecyclerView customRecyclerView;
    private LinearLayoutManager layoutManager;
    private TextView emptyListTextView;
    private UserDbHelper userDbHelper;
    private RelativeLayout progressbar;
    private SeriesListAdapter seriesListAdapter;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        watchState = getWatchState(args.getString(WatchState.WATCH_STATE_TYPE.name(), WatchState.WATCH_STATE.name()));
    }

    private WatchState getWatchState(String watchStateString) {
        if (watchStateString.equals(WatchState.WATCH_STATE.name()))
            return WatchState.WATCH_STATE;
        else
            return WatchState.WATCHED_STATE;
    }

    @Override
    public void onEpisodeWatchedClick(Series series, int position) {
        SeriesDbHelper.getInstance(getActivity()).episodeWatched(series);
        seriesListAdapter.updateSeries(series, position);
    }

    @Override
    public void onResume() {
        updateAdapter();

        super.onResume();
    }

    public void updateAdapter() {
        seriesListAdapter.updateDataSet();
    }

    public View getRecyclerView() {
        return customRecyclerView;
    }

    public RelativeLayout getMovieListProgressbar() {
        return progressbar;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();

        View watchlistView = initViews(inflater, container);

        if (activity != null) {
            seriesListAdapter = new SeriesListAdapter(this, activity, null, watchState, this);
            showMessageIfEmptyList();

            customRecyclerView.setLayoutManager(layoutManager);
            customRecyclerView.setAdapter(seriesListAdapter);

            FloatingActionButton fab = watchlistView.findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), SeriesSearchActivity.class);
                    activity.startActivity(intent);
                }
            });

            initSwipe();

            if (watchState == WatchState.WATCH_STATE) {
                activity.setTitle(R.string.watchList);
            } else {
                activity.setTitle(R.string.watchedlist);
            }
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null)
                actionBar.setSubtitle(R.string.series);
        }

        return watchlistView;
    }

    private View initViews(@NonNull LayoutInflater inflater, ViewGroup container) {
        View watchListView = inflater.inflate(R.layout.fragment_series_list, container, false);

        progressbar = watchListView.findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);

        emptyListTextView = watchListView.findViewById(R.id.info_text);

        customRecyclerView = watchListView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());

        customRecyclerView.setHasFixedSize(true);

        return watchListView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WatchState.WATCH_STATE_TYPE.name(), watchState.name());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            String currentState = savedInstanceState.getString(WatchState.WATCH_STATE_TYPE.name(), WatchState.WATCH_STATE.name());
            this.watchState = getWatchState(currentState);

            userDbHelper = UserDbHelper.getInstance(getActivity());
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = android.support.v7.appcompat.R.id.search_button; // I used the explicit layout ID of searchView's ImageView
        ImageView v = mSearchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_filter);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.start_movie_night, menu);

        MenuItem movieNight = menu.findItem(R.id.startMovieNight);
        movieNight.setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ((SeriesListAdapter) customRecyclerView.getAdapter()).getFilter().filter(newText);
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.startMovieNight:
                if (userDbHelper.getUser() != null) {
                    startMovieNight();
                } else {
                    FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager != null) {
                        new UserInputFragment().show(fragmentManager, "");
                    }
                }

                break;
            case R.id.filterAlphabetical:
                reorderLists(FilterType.ALPHABETICAL);
                break;
            case R.id.filterReleaseDate:
                reorderLists(FilterType.RELEASE_DATE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reorderLists(FilterType filterType) {
        progressbar.setVisibility(View.VISIBLE);
        customRecyclerView.enableScrolling(false);

        switch (filterType) {
            case ALPHABETICAL:
                seriesListAdapter.orderAlphabetical();
                break;
            case RELEASE_DATE:
                seriesListAdapter.orderByReleaseDate();
                break;
        }

        seriesListAdapter.notifyDataSetChanged();
        progressbar.setVisibility(View.GONE);
        customRecyclerView.enableScrolling(true);
    }

    private void startMovieNight() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, MovieNightActivity.class);
        activity.startActivity(intent);
    }

    @Override
    public void showMessageIfEmptyList() {
        if (seriesListAdapter.getDataSetSize() == 0) {
            customRecyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
            emptyListTextView.setText(getEmptyListMessageByState());
        } else {
            customRecyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    private int getEmptyListMessageByState() {
        if (watchState == WatchState.WATCH_STATE) {
            return R.string.noMoviesOnWatchList;
        } else {
            return R.string.noMoviesOnWatchedList;
        }
    }

    private void initSwipe() {
        //todo;
    }

    private enum FilterType {
        ALPHABETICAL, RELEASE_DATE
    }
}
