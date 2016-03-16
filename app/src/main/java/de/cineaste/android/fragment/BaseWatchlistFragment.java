package de.cineaste.android.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.cineaste.android.MovieClickListener;
import de.cineaste.android.MovieDetailActivity;
import de.cineaste.android.R;
import de.cineaste.android.adapter.BaseWatchlistAdapter;
import de.cineaste.android.adapter.WatchlistViewPagerAdapter;
import de.cineaste.android.adapter.WatchedlistAdapter;
import de.cineaste.android.adapter.WatchlistAdapter;
import de.cineaste.android.database.BaseDao;

public class BaseWatchlistFragment extends Fragment
        implements WatchlistViewPagerAdapter.WatchlistFragment, MovieClickListener {

    private String watchlistType;

    private RecyclerView baseWatchlistRecyclerView;
    private BaseWatchlistAdapter baseWatchlistAdapter;
    private TextView emptyListTextView;
    private SearchView searchView;

    public interface WatchlistFragmentType {
        String WATCHLIST_TYPE = "WatchlistType";
        String WATCH_LIST = "Watchlist";
        String WATCHED_LIST = "Watchedlist";
    }



    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        watchlistType = args.getString(WatchlistFragmentType.WATCHLIST_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View watchlistView = inflater.inflate(R.layout.fragment_base_watchlist, container, false);

        emptyListTextView = (TextView) watchlistView.findViewById(R.id.info_text);

        baseWatchlistRecyclerView =
                (RecyclerView) watchlistView.findViewById(R.id.basewatchlist_recycler_view);
        RecyclerView.LayoutManager baseWatchlistLayoutMgr = new LinearLayoutManager(getActivity());

        baseWatchlistRecyclerView.setHasFixedSize(true);

        setCorrectWatchlistAdapter();

        baseWatchlistRecyclerView.setLayoutManager(baseWatchlistLayoutMgr);
        baseWatchlistRecyclerView.setAdapter(baseWatchlistAdapter);

        return watchlistView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setCorrectWatchlistAdapter() {
        if (watchlistType.equals(WatchlistFragmentType.WATCH_LIST)) {
            baseWatchlistAdapter = new WatchlistAdapter(getActivity(), this, this);
            configureWatchlistVisibility();
        } else {
            baseWatchlistAdapter = new WatchedlistAdapter(getActivity(), this, this);
            configureWatchedlistVisibility();
        }
    }

    @Override
    public void configureWatchlistVisibility() {

        if (baseWatchlistAdapter.getItemCount() == 0) {
            baseWatchlistRecyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
            emptyListTextView.setText(R.string.noMoviesOnWatchList);
        } else {
            baseWatchlistRecyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void configureWatchedlistVisibility() {
        if (baseWatchlistAdapter.getTotalItemCount() == 0) {
            baseWatchlistRecyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
            emptyListTextView.setText(R.string.noMoviesOnWatchedList);
        } else {
            baseWatchlistRecyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WatchlistFragmentType.WATCHLIST_TYPE, watchlistType);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            this.watchlistType =
                    savedInstanceState.getString(WatchlistFragmentType.WATCHLIST_TYPE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate( R.menu.search_menu, menu );

        MenuItem searchItem = menu.findItem( R.id.action_search );


        if( searchItem != null ) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    ((BaseWatchlistAdapter)baseWatchlistRecyclerView.getAdapter()).filter(newText);
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onMovieClickListener(long movieId, View[] views) {
        int state;
        if (watchlistType.equals(WatchlistFragmentType.WATCH_LIST)) {
            state = R.string.watchlistState;
        } else {
            state = R.string.watchedlistState;
        }
        Intent intent = new Intent( getActivity(), MovieDetailActivity.class );
        intent.putExtra( BaseDao.MovieEntry._ID, movieId );
        intent.putExtra( getString( R.string.state ), state );

        if( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation( getActivity(),
                    Pair.create( views[0], "card" ),
                    Pair.create( views[1], "poster" )
            );
            getActivity().startActivity( intent, options.toBundle() );
        } else {
            getActivity().startActivity( intent );
           // getActivity().overridePendingTransition( R.anim.fade_out, R.anim.fade_in );
        }
    }
}
