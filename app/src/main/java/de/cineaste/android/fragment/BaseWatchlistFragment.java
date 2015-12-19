package de.cineaste.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.cineaste.android.R;
import de.cineaste.android.adapter.WatchedlistAdapter;
import de.cineaste.android.adapter.WatchlistAdapter;

public class BaseWatchlistFragment extends Fragment {

    private String mWatchlistType;

    private RecyclerView mBaseWatchlistRecyclerView;
    private RecyclerView.Adapter mBaseWatchlistAdapter;
    private RecyclerView.LayoutManager mBaseWatchlistLayoutMgr;

    public interface WatchlistFragmentType{
        String WATCHLIST_TYPE = "WatchlistType";
        String WATCH_LIST = "Watchlist";
        String WATCHED_LIST = "Watchedlist";
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mWatchlistType = args.getString(WatchlistFragmentType.WATCHLIST_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View watchlistView = inflater.inflate(R.layout.fragment_base_watchlist, container, false);

        mBaseWatchlistRecyclerView = (RecyclerView)watchlistView.findViewById(R.id.basewatchlist_recycler_view);

        return watchlistView;
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpView();
    }


    private void setUpView() {
        mBaseWatchlistLayoutMgr = new LinearLayoutManager(getActivity());

        mBaseWatchlistRecyclerView.setHasFixedSize( true );

        if( mWatchlistType.equals(WatchlistFragmentType.WATCH_LIST)){
            mBaseWatchlistAdapter = new WatchlistAdapter(getActivity());
        }
        else{
            mBaseWatchlistAdapter = new WatchedlistAdapter(getActivity());
        }

        mBaseWatchlistRecyclerView.setLayoutManager( mBaseWatchlistLayoutMgr );
        mBaseWatchlistRecyclerView.setAdapter( mBaseWatchlistAdapter );

    }
}
