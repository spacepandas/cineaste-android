package de.cineaste.android.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.cineaste.android.R;
import de.cineaste.android.fragment.BaseWatchlistFragment;

public class WatchlistViewPagerAdapter extends FragmentStatePagerAdapter {

    private final Context context;

    public WatchlistViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem( int position ) {
        switch ( position ) {
            case 0:
                BaseWatchlistFragment watchlistFragment = new BaseWatchlistFragment();
                Bundle bundle = new Bundle();
                bundle.putString(
                        BaseWatchlistFragment.WatchlistFragmentType.WATCHLIST_TYPE,
                        BaseWatchlistFragment.WatchlistFragmentType.WATCH_LIST );
                watchlistFragment.setArguments( bundle );
                return watchlistFragment;
            case 1:
                BaseWatchlistFragment watchedlistFragment = new BaseWatchlistFragment();
                Bundle bundle2 = new Bundle();
                bundle2.putString(
                        BaseWatchlistFragment.WatchlistFragmentType.WATCHLIST_TYPE,
                        BaseWatchlistFragment.WatchlistFragmentType.WATCHED_LIST );
                watchedlistFragment.setArguments( bundle2 );
                return watchedlistFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle( int position ) {
        String[] titles = new String[]{
                context.getString( R.string.watchList ),
                context.getString( R.string.watchedlist )
        };
        return titles[position];
    }
}