package de.cineaste.android.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Pair;
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
import de.cineaste.android.adapter.BaseListAdapter;
import de.cineaste.android.database.dbHelper.UserDbHelper;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.util.CustomRecyclerView;

import static android.app.ActivityOptions.makeSceneTransitionAnimation;

public abstract class BaseListFragment extends Fragment implements ItemClickListener, BaseListAdapter.DisplayMessage {

    WatchState watchState;
    CustomRecyclerView customRecyclerView;
    LinearLayoutManager layoutManager;
    private TextView emptyListTextView;
    private UserDbHelper userDbHelper;
    private RelativeLayout progressbar;

    public abstract void updateAdapter();
    protected abstract void initAdapter(Activity activity);
    protected abstract void initRecyclerView();
    protected abstract void initFab(Activity activity, View view);
    protected abstract int getSubtitle();
    protected abstract int getLayout();
    protected abstract void filterOnQueryTextChange(String newText);
    protected abstract void reorderEntries(FilterType filterType);
    protected abstract int getDataSetSize();
    protected abstract int getEmptyListMessageByState();
    protected abstract Intent createIntent(long itemId, int state, Activity activity);
    protected abstract ItemTouchHelper.Callback getCorrectCallBack();

    protected enum FilterType {
        ALPHABETICAL, RELEASE_DATE, RUNTIME
    }

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
    public void onResume() {
        updateAdapter();

        super.onResume();
    }

    @SuppressWarnings("unused")
    public View getRecyclerView() {
        return customRecyclerView;
    }

    public RelativeLayout getProgressbar() {
        return progressbar;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Activity activity = getActivity();

        View watchlistView = initViews(inflater, container);

        if (activity != null) {
            initAdapter(activity);
            showMessageIfEmptyList();

            initRecyclerView();

            initFab(activity, watchlistView);

            initSwipe();

            if (watchState == WatchState.WATCH_STATE) {
                activity.setTitle(R.string.watchList);
            } else {
                activity.setTitle(R.string.history);
            }
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null)
                actionBar.setSubtitle(getSubtitle());
        }


        return watchlistView;
    }

    private View initViews(@NonNull LayoutInflater inflater, ViewGroup container) {
        View watchlistView = inflater.inflate(getLayout(), container, false);

        progressbar = watchlistView.findViewById(R.id.progressBar);
        progressbar.setVisibility(View.GONE);

        emptyListTextView = watchlistView.findViewById(R.id.info_text);

        customRecyclerView = watchlistView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());

        customRecyclerView.setHasFixedSize(true);
        return watchlistView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WatchState.WATCH_STATE_TYPE.name(), watchState.name());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            String currentState = savedInstanceState.getString(WatchState.WATCH_STATE_TYPE.name(), WatchState.WATCH_STATE.name());
            this.watchState = getWatchState(currentState);
        }

        userDbHelper = UserDbHelper.getInstance(getActivity());
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
                    filterOnQueryTextChange(newText);
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
                reorderLists(BaseMovieListFragment.FilterType.ALPHABETICAL);
                break;
            case R.id.filterReleaseDate:
                reorderLists(BaseMovieListFragment.FilterType.RELEASE_DATE);
                break;
            case R.id.filterRunTime:
                reorderLists(FilterType.RUNTIME);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void reorderLists(FilterType filterType) {
        progressbar.setVisibility(View.VISIBLE);
        customRecyclerView.enableScrolling(false);

        reorderEntries(filterType);

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
        if (getDataSetSize() == 0) {
            customRecyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
            emptyListTextView.setText(getEmptyListMessageByState());
        } else {
            customRecyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClickListener(long itemId, View[] views) {
        if (!customRecyclerView.isScrollingEnabled()) {
            return;
        }
        int state;
        if (watchState == WatchState.WATCH_STATE) {
            state = R.string.watchlistState;
        } else {
            state = R.string.historyState;
        }
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Intent intent = createIntent(itemId, state, activity);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = makeSceneTransitionAnimation(activity,
                    Pair.create(views[0], "card"),
                    Pair.create(views[1], "poster")
            );
            activity.startActivity(intent, options.toBundle());
        } else {
            activity.startActivity(intent);
            // getActivity().overridePendingTransition( R.anim.fade_out, R.anim.fade_in );
        }
    }
    
    private void initSwipe() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(getCorrectCallBack());
        itemTouchHelper.attachToRecyclerView(customRecyclerView);
    }
}
