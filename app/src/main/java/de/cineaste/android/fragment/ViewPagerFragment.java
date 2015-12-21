package de.cineaste.android.fragment;

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

import de.cineaste.android.MainActivity;
import de.cineaste.android.R;
import de.cineaste.android.adapter.BaseWatchlistPagerAdapter;

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

        mPagerAdapter = new BaseWatchlistPagerAdapter( getChildFragmentManager(), getActivity() );

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
                MainActivity.startMovieNight( getFragmentManager() );
                break;
        }

        return super.onOptionsItemSelected( item );
    }
}