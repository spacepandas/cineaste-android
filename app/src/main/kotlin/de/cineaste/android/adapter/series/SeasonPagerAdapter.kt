package de.cineaste.android.adapter.series

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import de.cineaste.android.R
import de.cineaste.android.entity.series.Series
import de.cineaste.android.fragment.SeasonDetailFragment

class SeasonPagerAdapter(
    fm: FragmentManager,
    private val series: Series,
    private val resources: Resources
) :
    FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        val fragment = SeasonDetailFragment()
        val currentSeason = series.seasons[position]
        val args = Bundle()
        args.putLong("seasonId", currentSeason.id)
        args.putLong("seriesId", series.id)
        fragment.arguments = args

        return fragment
    }

    override fun getCount(): Int {
        return series.seasons.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return resources.getString(
            R.string.currentSeason,
            series.seasons[position].seasonNumber.toString()
        )
    }
}
