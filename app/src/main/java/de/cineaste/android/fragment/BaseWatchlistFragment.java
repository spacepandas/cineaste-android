package de.cineaste.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.WatchedlistAdapter;
import de.cineaste.android.adapter.WatchlistAdapter;

public class BaseWatchlistFragment extends Fragment {

    private String mWatchlistType;

    private RecyclerView mBaseWatchlistRecyclerView;
    private RecyclerView.Adapter mBaseWatchlistAdapter;
    private RecyclerView.LayoutManager mBaseWatchlistLayoutMgr;
    private TextView mTextview;

    public interface WatchlistFragmentType{
        String WATCHLIST_TYPE = "WatchlistType";
        String WATCH_LIST = "Watchlist";
        String WATCHED_LIST = "Watchedlist";
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments( args );
        mWatchlistType = args.getString(WatchlistFragmentType.WATCHLIST_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View watchlistView = inflater.inflate(R.layout.fragment_base_watchlist, container, false);

        mTextview = (TextView) watchlistView.findViewById( R.id.info_text );

        mBaseWatchlistRecyclerView = (RecyclerView)watchlistView.findViewById(R.id.basewatchlist_recycler_view);
        mBaseWatchlistLayoutMgr = new LinearLayoutManager(getActivity());

        mBaseWatchlistRecyclerView.setHasFixedSize( true );
//todo Refactor
        if( mWatchlistType.equals(WatchlistFragmentType.WATCH_LIST)){
            mBaseWatchlistAdapter = new WatchlistAdapter(getActivity());
            if( mBaseWatchlistAdapter.getItemCount() == 0) {
                mBaseWatchlistRecyclerView.setVisibility( View.GONE );
                mTextview.setVisibility( View.VISIBLE );
                mTextview.setText( R.string.noMoviesOnWatchList );
            }  else {
                mBaseWatchlistRecyclerView.setVisibility( View.VISIBLE );
                mTextview.setVisibility( View.GONE );
            }
        }
        else{
            mBaseWatchlistAdapter = new WatchedlistAdapter(getActivity());
            if( mBaseWatchlistAdapter.getItemCount() == 0) {
                mBaseWatchlistRecyclerView.setVisibility( View.GONE );
                mTextview.setVisibility( View.VISIBLE );
                mTextview.setText( R.string.noMoviesOnWatchedList );
            }  else {
                mBaseWatchlistRecyclerView.setVisibility( View.VISIBLE );
                mTextview.setVisibility( View.GONE );
            }
        }

        mBaseWatchlistRecyclerView.setLayoutManager( mBaseWatchlistLayoutMgr );
        mBaseWatchlistRecyclerView.setAdapter( mBaseWatchlistAdapter );

        return watchlistView;
    }
}
