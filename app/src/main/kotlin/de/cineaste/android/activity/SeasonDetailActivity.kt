package de.cineaste.android.activity

import android.content.Intent
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import com.squareup.picasso.Picasso
import de.cineaste.android.R
import de.cineaste.android.adapter.series.SeasonPagerAdapter
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.dbHelper.SeriesDbHelper
import de.cineaste.android.entity.series.Series
import de.cineaste.android.util.Constants

class SeasonDetailActivity : AppCompatActivity() {

    private var currentSeries: Series? = null
    private lateinit var poster: ImageView
    private lateinit var seriesDbHelper: SeriesDbHelper

    private var seasonId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_season_detail)

        seriesDbHelper = SeriesDbHelper.getInstance(this)

        poster = findViewById(R.id.poster_image_view)

        val intent = intent
        val seriesId = intent.getLongExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SERIES_ID, -1)
        seasonId = intent.getLongExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SEASON_NUMBER, -1)

        currentSeries = seriesDbHelper.getSeriesById(seriesId)

        val series = currentSeries

        series?.let { assignData(series) }

        initToolbar()
    }

    private fun assignData(series: Series) {
        val adapter = SeasonPagerAdapter(supportFragmentManager, series, resources)
        val viewPager = findViewById<ViewPager>(R.id.pager)

        viewPager.adapter = adapter
        viewPager.currentItem = currentSeasonIndex()
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // do nothing
            }

            override fun onPageSelected(position: Int) {
                setPoster(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // do nothing
            }
        })

        setPoster(currentSeasonIndex())
    }

    private fun setPoster(position: Int) {
        currentSeries?.seasons?.let { seasons ->
            val season = seasons[position]

            val posterPath = season.posterPath

            if (posterPath.isNullOrEmpty()) {
                Picasso.get()
                    .load(R.drawable.placeholder_poster)
                    .into(poster)
            } else {
                val posterUri = Constants.POSTER_URI_SMALL
                    .replace("<posterName>", posterPath)
                    .replace("<API_KEY>", getString(R.string.movieKey))
                Picasso.get()
                    .load(posterUri)
                    .error(R.drawable.placeholder_poster)
                    .into(poster)

                poster.setOnClickListener {
                    val intent = Intent(this@SeasonDetailActivity, PosterActivity::class.java)
                    intent.putExtra(PosterActivity.POSTER_PATH, posterPath)
                    startActivity(intent)
                }
            }
        }
    }

    private fun currentSeasonIndex(): Int {
        currentSeries?.seasons?.let { seasons ->
            for (i in seasons.indices) {
                if (seasons[i].id == seasonId) {
                    return i
                }
            }
        }
        return 0
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setTitleIfNeeded()
    }

    private fun setTitleIfNeeded() {
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar)
        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    val title = currentSeries?.name

                    title?.let {
                        collapsingToolbarLayout.title = title
                    }

                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    isShow = false
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                currentSeries?.let { series ->
                    val unwatchedEpisodes = seriesDbHelper.getUnWatchedEpisodesOfSeries(series.id)
                    if (unwatchedEpisodes.isEmpty() && !series.isInProduction) {
                        series.isWatched = true
                        seriesDbHelper.updateWatchState(series)
                    }
                    onBackPressed()
                    return true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
