package de.cineaste.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class BaseWatchlistPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] mFragments;
    private String[] mTitles;

    public BaseWatchlistPagerAdapter( FragmentManager fm, Fragment[] fragments, String[] titles ) {
        super( fm );
        this.mFragments = fragments;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem( int position ) {
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public CharSequence getPageTitle( int position ) {
        return mTitles[position];
    }
}
