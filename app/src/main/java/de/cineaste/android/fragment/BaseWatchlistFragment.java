package de.cineaste.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.cineaste.android.MainActivity;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.adapter.BaseWatchlistPagerAdapter;
import de.cineaste.android.adapter.WatchedlistAdapter;
import de.cineaste.android.adapter.WatchlistAdapter;
import de.cineaste.android.database.BaseDao;

public class BaseWatchlistFragment extends Fragment
        implements BaseWatchlistPagerAdapter.WatchlistFragment, MovieClickListener {

    private String watchlistType;

    private RecyclerView baseWatchlistRecyclerView;
    private RecyclerView.Adapter baseWatchlistAdapter;
    private TextView emptyListTextView;

    public interface WatchlistFragmentType {
        String WATCHLIST_TYPE = "WatchlistType";
        String WATCH_LIST = "Watchlist";
        String WATCHED_LIST = "Watchedlist";
    }

    @Override
    public void setArguments( Bundle args ) {
        super.setArguments( args );
        watchlistType = args.getString( WatchlistFragmentType.WATCHLIST_TYPE );
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

        if (watchlistType.equals(WatchlistFragmentType.WATCH_LIST)) {
            baseWatchlistAdapter = new WatchlistAdapter(getActivity(), this, this);
            setWatchlistAdapter();
        } else {
            baseWatchlistAdapter = new WatchedlistAdapter(getActivity(), this, this);
            setWatchedlistAdapter();
        }

        baseWatchlistRecyclerView.setLayoutManager(baseWatchlistLayoutMgr);
        baseWatchlistRecyclerView.setAdapter(baseWatchlistAdapter);

        return watchlistView;
    }

    public void setWatchlistAdapter() {

        if (baseWatchlistAdapter.getItemCount() == 0) {
            baseWatchlistRecyclerView.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
            emptyListTextView.setText(R.string.noMoviesOnWatchList);
        } else {
            baseWatchlistRecyclerView.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    public void setWatchedlistAdapter() {
        if (baseWatchlistAdapter.getItemCount() == 0) {
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
        if (savedInstanceState != null) {
            this.watchlistType =
                    savedInstanceState.getString(WatchlistFragmentType.WATCHLIST_TYPE);
        }
    }

    @Override
    public void onMovieClickListener(long movieId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseDao.MovieEntry._ID, movieId);
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(bundle);
        MainActivity.replaceFragment(getParentFragment().getFragmentManager(), fragment);
    }
}
