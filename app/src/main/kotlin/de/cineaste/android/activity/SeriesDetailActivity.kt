package de.cineaste.android.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import de.cineaste.android.R
import de.cineaste.android.adapter.series.SeriesDetailAdapter
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.dbHelper.SeriesDbHelper
import de.cineaste.android.entity.series.Series
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.network.SeriesCallback
import de.cineaste.android.network.SeriesLoader
import de.cineaste.android.util.Constants
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SeriesDetailActivity : AppCompatActivity(), ItemClickListener,
    SeriesDetailAdapter.SeriesStateManipulationClickListener, View.OnClickListener {

    private var state: Int = 0
    private var seriesId: Long = 0

    private lateinit var seriesDbHelper: SeriesDbHelper
    private lateinit var seriesLoader: SeriesLoader
    private var currentSeries: Series? = null
    private lateinit var progressBar: View
    private lateinit var poster: ImageView
    private lateinit var fab: FloatingActionButton
    private lateinit var layout: RecyclerView
    private lateinit var updateCallBack: Runnable
    private lateinit var adapter: SeriesDetailAdapter

    override fun onClick(v: View) {
        if (v.id == R.id.poster) {
            currentSeries?.posterPath?.let { posterPath ->
                val intent = Intent(this@SeriesDetailActivity, PosterActivity::class.java)
                intent.putExtra(PosterActivity.POSTER_PATH, posterPath)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.more_info -> {
                currentSeries?.let { series ->
                    val tmdbUri = Constants.THE_MOVIE_DB_SERIES_URI
                        .replace("<SERIES_ID>", series.id.toString())
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tmdbUri))
                    startActivity(browserIntent)
                }
            }

            R.id.share -> {
                currentSeries?.let { series ->
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBodyText = "${series.name} ${Constants.THE_MOVIE_DB_SERIES_URI.replace(
                        "<SERIES_ID>",
                        series.id.toString()
                    )}"
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_series))
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
                    startActivity(
                        Intent.createChooser(
                            sharingIntent,
                            getString(R.string.share_series)
                        )
                    )
                }
                return true
            }
        }
        return true
    }

    override fun onDeleteClicked() {
        layout.removeCallbacks(updateCallBack)
        seriesDbHelper.delete(seriesId)
        currentSeries = null
        onBackPressed()
    }

    override fun onAddToHistoryClicked() {
        var seriesCallback: SeriesCallback? = null

        when (state) {
            R.string.searchState ->

                seriesCallback = object : SeriesCallback {
                    override fun onFailure() {
                    }

                    override fun onSuccess(series: Series) {
                        showDialogIfNeeded(series)
                    }
                }
            R.string.watchlistState -> {
                currentSeries?.let { series ->
                    showDialogIfNeeded(series)
                }
            }
        }

        seriesCallback?.let {
            seriesLoader.loadCompleteSeries(seriesId, seriesCallback)

            showAddToast()

            onBackPressed()
        }
    }

    private fun showAddToast() {
        currentSeries?.let { series ->
            Toast.makeText(
                this, this.resources.getString(
                    R.string.movieAdd,
                    series.name
                ), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDialogIfNeeded(series: Series) {
        if (series.isInProduction) {
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle(getString(R.string.seriesSeenHeadline, series.name))
            alertBuilder.setMessage(R.string.seriesStillInProduction)
            alertBuilder.setPositiveButton(R.string.ok) { _, _ -> moveBetweenLists(series) }
            alertBuilder.setNegativeButton(R.string.cancel) { _, _ ->
                // do nothing
            }

            alertBuilder.create().show()
        } else {
            moveBetweenLists(series)
        }
    }

    private fun moveBetweenLists(series: Series) {
        if (state == R.string.searchState) {
            seriesDbHelper.addToHistory(series)
        } else if (state == R.string.watchlistState) {
            seriesDbHelper.moveToHistory(series)
        }

        onBackPressed()
    }

    override fun onAddToWatchClicked() {
        val callback: SeriesCallback?

        when (state) {
            R.string.searchState -> callback = object : SeriesCallback {
                override fun onFailure() {
                }

                override fun onSuccess(series: Series) {
                    seriesDbHelper.addToWatchList(series)
                }
            }
            R.string.historyState -> {
                val series = currentSeries
                series?.let {
                    seriesDbHelper.moveToWatchList(series)
                }
                callback = null
            }
            else -> callback = null
        }

        callback?.let {
            seriesLoader.loadCompleteSeries(seriesId, it)

            showAddToast()
        }

        onBackPressed()
    }

    override fun onItemClickListener(itemId: Long, views: Array<View>) {
        if (state != R.string.searchState) {
            val intent = Intent(this@SeriesDetailActivity, SeasonDetailActivity::class.java)
            intent.putExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SERIES_ID, seriesId)
            intent.putExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SEASON_NUMBER, itemId)

            startActivity(intent)
        } else {
            val snackBar = Snackbar.make(
                layout,
                R.string.notAvailableDuringSearch, Snackbar.LENGTH_SHORT
            )
            snackBar.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series_detail)

        seriesDbHelper = SeriesDbHelper.getInstance(this)
        seriesLoader = SeriesLoader(this)

        val intent = intent
        seriesId = intent.getLongExtra(BaseDao.SeriesEntry.ID, -1)
        state = intent.getIntExtra(getString(R.string.state), -1)

        initViews()

        updateCallBack = getUpdateCallBack()
        autoUpdate()

        currentSeries = seriesDbHelper.getSeriesById(seriesId)
        val series = currentSeries
        if (series == null) {
            progressBar.visibility = View.VISIBLE
            fab.hide()
            loadRequestedSeries()
        } else {
            progressBar.visibility = View.GONE
            if (state == R.string.watchlistState)
                fab.show()
            assignData(series)
        }

        initToolbar()

        currentSeries?.backdropPath?.let { backdropPath ->
            poster.setOnClickListener {
                val myIntent = Intent(this@SeriesDetailActivity, PosterActivity::class.java)
                myIntent.putExtra(PosterActivity.POSTER_PATH, backdropPath)
                slideOut()
                startActivity(myIntent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        currentSeries?.let {
            if (state != R.string.searchState) {
                slideIn()
            }
        }
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
        fab = findViewById(R.id.fab)
        val layoutManager = LinearLayoutManager(this)
        poster = findViewById(R.id.movie_poster)
        layout = findViewById(R.id.overlay)
        layout.layoutManager = layoutManager
        layout.setHasFixedSize(true)

        if (state == R.string.watchlistState) {

            fab.show()
            fab.setOnClickListener {
                currentSeries?.let {
                    seriesDbHelper.episodeWatched(it)
                    currentSeries = seriesDbHelper.getSeriesById(it.id)
                    assignData(it)
                }
            }
        } else {
            fab.hide()
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

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
                    val series = currentSeries
                    series?.let {
                        collapsingToolbarLayout.title = series.name
                    }
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    isShow = false
                }
            }
        })
    }

    private fun autoUpdate() {
        layout.removeCallbacks(updateCallBack)
        layout.postDelayed(updateCallBack, 1000)
    }

    private fun getUpdateCallBack(): Runnable {
        return Runnable { updateSeries() }
    }

    private fun updateSeries() {
        if (state != R.string.searchState) {

            seriesLoader.loadCompleteSeries(seriesId,
                object : SeriesCallback {
                    override fun onFailure() {
                        GlobalScope.launch(Main) { showNetworkError() }
                    }

                    override fun onSuccess(series: Series) {
                        val oldSeries = currentSeries
                        oldSeries?.let {
                            series.isWatched = oldSeries.isWatched
                            series.listPosition = oldSeries.listPosition
                        }
                        seriesDbHelper.update(series)
                        GlobalScope.launch(Main) {
                            setPoster(series)
                            adapter.updateSeries(series)
                        }
                    }
                }
            )
        }
    }

    private fun assignData(series: Series) {
        currentSeries = series
        setPoster(series)

        adapter = SeriesDetailAdapter(series, this, state, this, this)
        layout.adapter = adapter
    }

    private fun setPoster(series: Series) {
        val posterUri = Constants.POSTER_URI_ORIGINAL
            .replace("<posterName>", series.backdropPath ?: "/")
            .replace("<API_KEY>", getString(R.string.movieKey))
        Picasso.get()
            .load(posterUri)
            .error(R.drawable.placeholder_poster)
            .into(poster)
    }

    private fun loadRequestedSeries() {
        seriesLoader.loadCompleteSeries(seriesId, object : SeriesCallback {
            override fun onFailure() {
            }

            override fun onSuccess(series: Series) {
                GlobalScope.launch(Main) {
                    currentSeries = series
                    assignData(series)
                    progressBar.visibility = View.GONE
                    if (state == R.string.watchlistState)
                        fab.show()

                    slideIn()
                }
            }
        })
    }

    private fun slideIn() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.to_top)
        animation.interpolator = AccelerateDecelerateInterpolator()
        layout.startAnimation(animation)
        if (state != R.string.searchState) {
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                }

                override fun onAnimationEnd(animation: Animation) {
                    val series = seriesDbHelper.getSeriesById(seriesId)
                    series?.let {
                        currentSeries = series
                        adapter.updateSeries(series)
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {
                }
            })
        }
    }

    private fun slideOut() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.to_bottom)
        animation.interpolator = AccelerateDecelerateInterpolator()
        layout.startAnimation(animation)
    }

    override fun onBackPressed() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.to_bottom)
        animation.interpolator = AccelerateDecelerateInterpolator()
        layout.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }

            override fun onAnimationEnd(animation: Animation) {
                super@SeriesDetailActivity.onBackPressed()
                layout.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
    }

    private fun showNetworkError() {
        val snackBar = Snackbar
            .make(layout, R.string.noInternet, Snackbar.LENGTH_LONG)
        snackBar.show()
    }
}
