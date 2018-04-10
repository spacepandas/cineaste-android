package de.cineaste.android.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import de.cineaste.android.R
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.network.NetworkCallback
import de.cineaste.android.network.NetworkClient
import de.cineaste.android.network.NetworkRequest
import de.cineaste.android.network.NetworkResponse
import de.cineaste.android.util.Constants
import de.cineaste.android.util.DateAwareGson
import java.text.SimpleDateFormat
import java.util.*

class MovieDetailActivity : AppCompatActivity() {

    private var gson: Gson? = null
    private var state: Int = 0
    private var poster: ImageView? = null

    private var movieDbHelper: MovieDbHelper? = null
    private var movieId: Long = 0
    private var currentMovie: Movie? = null
    private var rating: TextView? = null
    private var movieTitle: TextView? = null
    private var movieReleaseDate: TextView? = null
    private var movieRuntime: TextView? = null
    private var layout: NestedScrollView? = null
    private var updateCallBack: Runnable? = null

    private val updateCallback: Runnable
        get() = Runnable { updateMovie() }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.detail_menu, menu)
        val toWatchList = menu.findItem(R.id.action_to_watchlist)
        val toHistory = menu.findItem(R.id.action_to_history)
        val delete = menu.findItem(R.id.action_delete)

        for (i in 0 until menu.size()) {
            val drawable = menu.getItem(i).icon
            if (drawable != null) {
                drawable.mutate()
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            }
        }

        when (state) {
            R.string.searchState -> {
                delete.isVisible = false
                toHistory.isVisible = true
                toWatchList.isVisible = true
            }
            R.string.historyState -> {
                delete.isVisible = true
                toHistory.isVisible = false
                toWatchList.isVisible = true
            }
            R.string.watchlistState -> {
                delete.isVisible = true
                toHistory.isVisible = true
                toWatchList.isVisible = false
            }
        }


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.action_delete -> {
                onDeleteClicked()
                return true
            }
            R.id.action_to_history -> {
                onAddToHistoryClicked()
                return true
            }
            R.id.action_to_watchlist -> {
                onAddToWatchClicked()
                return true
            }
        }
        return true
    }

    private fun onDeleteClicked() {
        movieDbHelper!!.deleteMovieFromWatchlist(currentMovie!!)
        layout!!.removeCallbacks(updateCallBack)
        onBackPressed()
    }

    private fun onAddToHistoryClicked() {
        var callback: NetworkCallback? = null

        when (state) {
            R.string.searchState -> callback = object : NetworkCallback {
                override fun onFailure() {

                }

                override fun onSuccess(response: NetworkResponse) {
                    val movie = gson!!.fromJson(response.responseReader, Movie::class.java)
                    movie.isWatched = true
                    movieDbHelper!!.createOrUpdate(movie)
                }
            }
            R.string.watchlistState -> {
                currentMovie!!.isWatched = true
                movieDbHelper!!.createOrUpdate(currentMovie!!)
            }
        }


        if (callback != null) {
            val client = NetworkClient(NetworkRequest(resources).getMovie(currentMovie!!.id))
            client.sendRequest(callback)
            Toast.makeText(this, this.resources.getString(R.string.movieAdd,
                    currentMovie!!.title), Toast.LENGTH_SHORT).show()
        }

        onBackPressed()

    }

    private fun onAddToWatchClicked() {
        var callback: NetworkCallback? = null

        when (state) {
            R.string.searchState -> callback = object : NetworkCallback {
                override fun onFailure() {

                }

                override fun onSuccess(response: NetworkResponse) {
                    movieDbHelper!!.createOrUpdate(gson!!.fromJson(response.responseReader, Movie::class.java))
                }
            }
            R.string.historyState -> {
                currentMovie!!.isWatched = false
                movieDbHelper!!.createOrUpdate(currentMovie!!)
            }
        }

        if (callback != null) {
            val client = NetworkClient(NetworkRequest(resources).getMovie(currentMovie!!.id))
            client.sendRequest(callback)
            Toast.makeText(this, this.resources.getString(R.string.movieAdd,
                    currentMovie!!.title), Toast.LENGTH_SHORT).show()
        }

        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        gson = DateAwareGson().gson

        val intent = intent
        movieId = intent.getLongExtra(BaseDao.MovieEntry.ID, -1)
        state = intent.getIntExtra(getString(R.string.state), -1)

        initViews()

        movieDbHelper = MovieDbHelper.getInstance(this)

        updateCallBack = updateCallback
        autoUpdateMovie()

        currentMovie = movieDbHelper!!.readMovie(movieId)
        if (currentMovie == null) {
            loadRequestedMovie()
        } else {
            assignData(currentMovie!!)
        }

        initToolbar()

        poster!!.setOnClickListener {
            val myIntent = Intent(this@MovieDetailActivity, PosterActivity::class.java)
            myIntent.putExtra(PosterActivity.POSTER_PATH, currentMovie!!.posterPath)
            slideOut()
            startActivity(myIntent)
        }
    }

    private fun initViews() {
        movieReleaseDate = findViewById(R.id.movieReleaseDate)
        poster = findViewById(R.id.movie_poster)
        rating = findViewById(R.id.rating)
        movieTitle = findViewById(R.id.movieTitle)
        movieRuntime = findViewById(R.id.movieRuntime)
        layout = findViewById(R.id.overlay)

        val deleteBtn = findViewById<Button>(R.id.delete_button)
        val historyBtn = findViewById<Button>(R.id.history_button)
        val watchListBtn = findViewById<Button>(R.id.to_watchlist_button)

        when (state) {
            R.string.searchState -> {
                deleteBtn.visibility = View.GONE
                historyBtn.visibility = View.VISIBLE
                watchListBtn.visibility = View.VISIBLE
            }
            R.string.historyState -> {
                deleteBtn.visibility = View.VISIBLE
                historyBtn.visibility = View.GONE
                watchListBtn.visibility = View.VISIBLE
            }
            R.string.watchlistState -> {
                deleteBtn.visibility = View.VISIBLE
                historyBtn.visibility = View.VISIBLE
                watchListBtn.visibility = View.GONE
            }
        }

        deleteBtn.setOnClickListener { onDeleteClicked() }

        historyBtn.setOnClickListener { onAddToHistoryClicked() }

        watchListBtn.setOnClickListener { onAddToWatchClicked() }

    }

    override fun onResume() {
        super.onResume()
        slideIn()
    }

    private fun loadRequestedMovie() {
        val client = NetworkClient(NetworkRequest(resources).getMovie(movieId))
        client.sendRequest(object : NetworkCallback {
            override fun onFailure() {

            }

            override fun onSuccess(response: NetworkResponse) {
                val movie = gson!!.fromJson(response.responseReader, Movie::class.java)
                runOnUiThread {
                    currentMovie = movie
                    assignData(movie)
                }
            }
        })
    }

    private fun assignData(currentMovie: Movie) {
        val movieDescription = findViewById<TextView>(R.id.movie_description)

        val description = currentMovie.description
        movieDescription.text = if (description == null || description.isEmpty())
            getString(R.string.noDescription)
        else
            description

        movieTitle!!.text = currentMovie.title
        if (currentMovie.releaseDate != null) {
            movieReleaseDate!!.text = convertDate(currentMovie.releaseDate)
            movieReleaseDate!!.visibility = View.VISIBLE
        } else {
            movieReleaseDate!!.visibility = View.GONE
        }
        movieRuntime!!.text = getString(R.string.runtime, currentMovie.runtime)
        rating!!.text = currentMovie.voteAverage.toString()


        val posterUri = Constants.POSTER_URI_SMALL
                .replace("<posterName>", if (currentMovie.posterPath != null)
                    currentMovie.posterPath!!
                else
                    "/")
                .replace("<API_KEY>", getString(R.string.movieKey))
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(poster)
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
            internal var isShow = true
            internal var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = currentMovie!!.title
                    movieTitle!!.visibility = View.GONE

                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    movieTitle!!.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
    }

    private fun autoUpdateMovie() {
        layout!!.removeCallbacks(updateCallBack)
        layout!!.postDelayed(updateCallBack, 1000)
    }

    private fun updateMovie() {
        if (state != R.string.searchState) {
            val client = NetworkClient(NetworkRequest(resources).getMovie(movieId))
            client.sendRequest(object : NetworkCallback {
                override fun onFailure() {
                    runOnUiThread { showNetworkError() }
                }

                override fun onSuccess(response: NetworkResponse) {
                    val movie = gson!!.fromJson(response.responseReader, Movie::class.java)
                    runOnUiThread {
                        assignData(movie)
                        updateMovieDetails(movie)
                        movieDbHelper!!.createOrUpdate(currentMovie!!)
                    }
                }
            })
        }
    }

    private fun updateMovieDetails(movie: Movie) {
        currentMovie!!.title = movie.title
        currentMovie!!.runtime = movie.runtime
        currentMovie!!.voteAverage = movie.voteAverage
        currentMovie!!.voteCount = movie.voteCount
        currentMovie!!.description = movie.description
        currentMovie!!.posterPath = movie.posterPath
        currentMovie!!.releaseDate = movie.releaseDate
    }

    private fun slideIn() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.to_top)
        animation.interpolator = AccelerateDecelerateInterpolator()
        layout!!.startAnimation(animation)
    }

    private fun slideOut() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.to_bottom)
        animation.interpolator = AccelerateDecelerateInterpolator()
        layout!!.startAnimation(animation)
    }

    override fun onBackPressed() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.to_bottom)
        animation.interpolator = AccelerateDecelerateInterpolator()
        layout!!.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                super@MovieDetailActivity.onBackPressed()
                layout!!.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })

    }

    private fun showNetworkError() {
        val snackbar = Snackbar
                .make(layout!!, R.string.noInternet, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun convertDate(date: Date?): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        return simpleDateFormat.format(date)
    }
}
