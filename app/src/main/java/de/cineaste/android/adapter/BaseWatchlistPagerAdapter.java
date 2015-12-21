package de.cineaste.android.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.cineaste.android.R;
import de.cineaste.android.fragment.BaseWatchlistFragment;

public class BaseWatchlistPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments;
    private String[] titles;
    private Context context;

    public BaseWatchlistPagerAdapter( FragmentManager fm, Context context ) {
        super( fm );
        this.context = context;
        initiateAdapter();
    }

    private void initiateAdapter(){
        fragments = new Fragment[]{new BaseWatchlistFragment(), new BaseWatchlistFragment()};
        Bundle bundle = new Bundle();
        bundle.putString( BaseWatchlistFragment.WatchlistFragmentType.WATCHLIST_TYPE, BaseWatchlistFragment.WatchlistFragmentType.WATCH_LIST );
        fragments[0].setArguments( bundle );

        bundle = new Bundle();
        bundle.putString( BaseWatchlistFragment.WatchlistFragmentType.WATCHLIST_TYPE, BaseWatchlistFragment.WatchlistFragmentType.WATCHED_LIST );
        fragments[1].setArguments( bundle );

        titles = new String[]{ context.getString(R.string.watchList) , context.getString( R.string.watchedlist) };

    }

    @Override
    public Fragment getItem( int position ) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle( int position ) {
        return titles[position];
    }
}
