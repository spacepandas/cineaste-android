package de.cineaste.android.adapter;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.cineaste.android.R;
import de.cineaste.android.entity.Season;
import de.cineaste.android.entity.Series;
import de.cineaste.android.fragment.SeasonDetailFragment;

public class SeasonPagerAdapter extends FragmentStatePagerAdapter {

    private Series series;
    private Resources resources;

    public SeasonPagerAdapter(FragmentManager fm, Series series, Resources resources) {
        super(fm);
        this.series = series;
        this.resources = resources;
    }

    @Override
    public Fragment getItem(int position) {
        Season currentSeason = series.getSeasons().get(position);
        Fragment fragment = new SeasonDetailFragment();
        Bundle args = new Bundle();
        args.putInt("seasonNr", currentSeason.getSeasonNumber());
        args.putLong("seasonId", currentSeason.getId());
        args.putLong("seriesId", series.getId());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return series.getSeasons().size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return resources.getString(R.string.currentSeason, String.valueOf(series.getSeasons().get(position).getSeasonNumber()));
    }
}
