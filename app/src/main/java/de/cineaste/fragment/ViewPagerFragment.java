package de.cineaste.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import de.cineaste.MainActivity;
import de.cineaste.R;
import de.cineaste.adapter.BaseWatchlistPagerAdapter;

public class ViewPagerFragment extends Fragment {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private FloatingActionButton fab;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu( true );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        View view = inflater.inflate( R.layout.fragment_view_pager, container, false );


        mViewPager = (ViewPager) view.findViewById( R.id.basewatchlist_pager );

        Fragment[] fragments = {new BaseWatchlistFragment(), new BaseWatchlistFragment()};
        Bundle bundle = new Bundle();
        bundle.putString( BaseWatchlistFragment.WatchlistFragmentType.WATCHLIST_TYPE, BaseWatchlistFragment.WatchlistFragmentType.WATCH_LIST );
        fragments[0].setArguments( bundle );

        bundle = new Bundle();
        bundle.putString( BaseWatchlistFragment.WatchlistFragmentType.WATCHLIST_TYPE, BaseWatchlistFragment.WatchlistFragmentType.WATCHED_LIST );
        fragments[1].setArguments( bundle );

        String[] titles = {getResources().getString( R.string.watchList ), getResources().getString( R.string.watchedlist )};

        mPagerAdapter = new BaseWatchlistPagerAdapter( getFragmentManager(), fragments, titles );

        mViewPager.setAdapter( mPagerAdapter );

        fab = (FloatingActionButton) view.findViewById( R.id.fab );

        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                MainActivity.replaceFragment( getFragmentManager(), new SearchFragment() );
            }
        } );

        return view;
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
        inflater.inflate( R.menu.start_movie_night, menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.startMovieNight:
                MainActivity.replaceFragment( getFragmentManager(), new MovieNightFragment() );
                break;
        }

        return super.onOptionsItemSelected( item );
    }
}