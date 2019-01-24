package de.cineaste.android.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.squareup.picasso.Picasso
import de.cineaste.android.R
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.network.MovieCallback
import de.cineaste.android.network.MovieLoader
import de.cineaste.android.util.Constants
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class MovieDetailActivity : AppCompatActivity() {

    private var state: Int = 0
    private lateinit var poster: ImageView

    private lateinit var movieDbHelper: MovieDbHelper
    private var movieId: Long = 0
    private var currentMovie: Movie? = null
    private lateinit var progressBar: View
    private lateinit var rating: TextView
    private lateinit var movieTitle: TextView
    private lateinit var movieReleaseDate: TextView
    private lateinit var movieRuntime: TextView
    private lateinit var layout: NestedScrollView
    private lateinit var updateCallBack: Runnable

    private val updateCallback: Runnable
        get() = Runnable { updateMovie() }

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
                val movie = currentMovie
                movie?.let {
                    val tmdbUri = Constants.THE_MOVIE_DB_MOVIES_URI
                            .replace("<MOVIE_ID>", movie.id.toString())
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tmdbUri))
                    startActivity(browserIntent)
                }
            }

            R.id.share -> {
                currentMovie?.let { movie ->
                    val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBodyText = "${movie.title} ${Constants.THE_MOVIE_DB_MOVIES_URI
                            .replace("<MOVIE_ID>", movie.id.toString())}"
                    sharingIntent.putExtra(
                            android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_movie)
                    )
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText)
                    startActivity(
                            Intent.createChooser(sharingIntent, getString(R.string.share_movie))
                    )
                }
                return true
            }
        }
        return true
    }

    private fun onDeleteClicked() {
        currentMovie?.let { movie ->
            movieDbHelper.deleteMovieFromWatchlist(movie)
            layout.removeCallbacks(updateCallBack)
            onBackPressed()
        }
    }

    private fun onAddToHistoryClicked() {
        var callback: MovieCallback? = null

        when (state) {
            R.string.searchState -> callback = object : MovieCallback {
                override fun onFailure() {
                }

                override fun onSuccess(movie: Movie) {
                    movie.isWatched = true
                    movieDbHelper.createOrUpdate(movie)
                }
            }
            R.string.watchlistState -> {
                currentMovie?.let { movie ->
                    movie.isWatched = true
                    movieDbHelper.createOrUpdate(movie)
                }
            }
        }

        if (callback != null) {
            MovieLoader(this).loadLocalizedMovie(movieId, Locale.getDefault(), callback)
            currentMovie?.title?.let { title ->
                Toast.makeText(this, this.resources.getString(R.string.movieAdd,
                        title), Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressed()
    }

    private fun onAddToWatchClicked() {
        var callback: MovieCallback? = null

        when (state) {
            R.string.searchState -> callback = object : MovieCallback {
                override fun onFailure() {
                }

                override fun onSuccess(movie: Movie) {
                    movieDbHelper.createOrUpdate(movie)
                }
            }
            R.string.historyState -> {
                val movie = currentMovie
                movie?.let {
                    movie.isWatched = false
                    movieDbHelper.createOrUpdate(movie)
                }
            }
        }

        if (callback != null) {
            MovieLoader(this).loadLocalizedMovie(movieId, Locale.getDefault(), callback)
            currentMovie?.let { movie ->
                Toast.makeText(this, this.resources.getString(R.string.movieAdd,
                        movie.title), Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val intent = intent
        movieId = intent.getLongExtra(BaseDao.MovieEntry.ID, -1)
        state = intent.getIntExtra(getString(R.string.state), -1)

        initViews()

        movieDbHelper = MovieDbHelper.getInstance(this)

        updateCallBack = updateCallback
        autoUpdateMovie()

        currentMovie = movieDbHelper.readMovie(movieId)
        val movie = currentMovie
        if (movie == null) {
            progressBar.visibility = View.VISIBLE
            loadRequestedMovie()
        } else {
            progressBar.visibility = View.GONE
            assignData(movie)
        }

        initToolbar()

        currentMovie?.posterPath?.let { posterPath ->
            poster.setOnClickListener {
                val myIntent = Intent(this@MovieDetailActivity, PosterActivity::class.java)
                myIntent.putExtra(PosterActivity.POSTER_PATH, posterPath)
                slideOut()
                startActivity(myIntent)
            }
        }
    }

    private fun initViews() {
        progressBar = findViewById(R.id.progressBar)
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
        currentMovie?.let {
            if (state != R.string.searchState) {
                slideIn()
            }
        }
    }

    private fun loadRequestedMovie() {
        MovieLoader(this).loadLocalizedMovie(movieId, Locale.getDefault(), object : MovieCallback {
            override fun onFailure() {
            }

            override fun onSuccess(movie: Movie) {
                GlobalScope.launch(Main) {
                    currentMovie = movie
                    assignData(movie)
                    progressBar.visibility = View.GONE
                    slideIn()
                }
            }
        })
    }

    private fun assignData(currentMovie: Movie) {
        val movieDescription = findViewById<TextView>(R.id.movie_description)

        val description = currentMovie.description
        movieDescription.text = if (description.isEmpty())
            getString(R.string.noDescription)
        else
            description

        movieTitle.text = currentMovie.title
        if (currentMovie.releaseDate != null) {
            movieReleaseDate.text = convertDate(currentMovie.releaseDate)
            movieReleaseDate.visibility = View.VISIBLE
        } else {
            movieReleaseDate.visibility = View.GONE
        }
        movieRuntime.text = getString(R.string.runtime, currentMovie.runtime)
        rating.text = currentMovie.voteAverage.toString()

        val posterUri = Constants.POSTER_URI_SMALL
                .replace("<posterName>", currentMovie.posterPath ?: "/")
                .replace("<API_KEY>", getString(R.string.movieKey))
        Picasso.get()
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
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    val title = currentMovie?.title
                    title?.let {
                        collapsingToolbarLayout.title = title
                    }
                    movieTitle.visibility = View.GONE

                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    movieTitle.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
    }

    private fun autoUpdateMovie() {
        layout.removeCallbacks(updateCallBack)
        layout.postDelayed(updateCallBack, 1000)
    }

    private fun updateMovie() {
        if (state != R.string.searchState) {
            MovieLoader(this).loadLocalizedMovie(movieId, Locale.getDefault(), object : MovieCallback {
                override fun onFailure() {
                    GlobalScope.launch(Main) { showNetworkError() }
                }

                override fun onSuccess(movie: Movie) {
                    GlobalScope.launch(Main) {
                        assignData(movie)
                        updateMovieDetails(movie)
                        movieDbHelper.createOrUpdate(currentMovie ?: movie)
                    }
                }
            })
        }
    }

    private fun updateMovieDetails(movie: Movie) {
        val oldMovie = currentMovie
        oldMovie?.let {
            val updatedMovie = Movie(
                    id = oldMovie.id,
                    posterPath = movie.posterPath,
                    title = movie.title,
                    runtime = movie.runtime,
                    voteAverage = movie.voteAverage,
                    voteCount = movie.voteCount,
                    description = movie.description,
                    watched = oldMovie.isWatched,
                    watchedDate = oldMovie.watchedDate,
                    releaseDate = movie.releaseDate,
                    listPosition = oldMovie.listPosition
            )

            currentMovie = updatedMovie
        }
    }

    private fun slideIn() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.to_top)
        animation.interpolator = AccelerateDecelerateInterpolator()
        layout.startAnimation(animation)
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
                super@MovieDetailActivity.onBackPressed()
                layout.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })
    }

    private fun showNetworkError() {
        val snackbar = Snackbar
                .make(layout, R.string.noInternet, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun convertDate(date: Date?): String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
        return simpleDateFormat.format(date)
    }
}
