package de.cineaste.android.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import de.cineaste.android.R
import de.cineaste.android.adapter.ResultAdapter
import de.cineaste.android.database.NearbyMessageHandler
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.entity.movie.MatchingResultMovie
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.movie.MovieDto
import de.cineaste.android.entity.movie.NearbyMessage
import de.cineaste.android.network.MovieCallback
import de.cineaste.android.network.MovieLoader
import de.cineaste.android.util.MultiList
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class ResultActivity : AppCompatActivity(), ResultAdapter.OnMovieSelectListener {

    private var nearbyMessages: MutableList<NearbyMessage> = mutableListOf()
    private lateinit var movieDbHelper: MovieDbHelper

    private val resultMovies: ArrayList<MatchingResultMovie>
        get() {
            val results = ArrayList<MatchingResultMovie>()
            val multiList = MultiList()
            multiList.addAll(movies)

            for (multiListEntry in multiList.sortedList()) {
                results.add(MatchingResultMovie(multiListEntry.movieDto, multiListEntry.counter))
            }

            return results
        }

    private val movies: ArrayList<MovieDto>
        get() {
            val movies = ArrayList<MovieDto>()

            for (current in nearbyMessages) {
                movies.addAll(current.movies.sortedList().map { it.movieDto })
            }

            return movies
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        nearbyMessages.addAll(NearbyMessageHandler.getMessages())

        movieDbHelper = MovieDbHelper.getInstance(this)

        initToolbar()

        val result = findViewById<RecyclerView>(R.id.result_list)

        val llm = LinearLayoutManager(this)
        llm.orientation = RecyclerView.VERTICAL
        result.layoutManager = llm
        result.itemAnimator = DefaultItemAnimator()

        val resultAdapter = ResultAdapter(
            resultMovies,
            this
        )
        result.adapter = resultAdapter
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setTitle(R.string.result)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onMovieSelectListener(position: Int) {

        val selectedMovieId = resultMovies[position].id
        val selectedMovie = movieDbHelper.readMovie(selectedMovieId)

        if (selectedMovie == null) {
            MovieLoader(this).loadLocalizedMovie(
                resultMovies[position].id,
                Locale.getDefault(),
                (object : MovieCallback {
                    override fun onFailure() {
                    }

                    override fun onSuccess(movie: Movie) {
                        GlobalScope.launch(Main) { updateMovie(movie) }
                    }
                })
            )
        } else {
            updateMovie(selectedMovie)
        }
    }

    private fun updateMovie(movie: Movie) {
        movie.isWatched = true
        movieDbHelper.createOrUpdate(movie)
    }
}