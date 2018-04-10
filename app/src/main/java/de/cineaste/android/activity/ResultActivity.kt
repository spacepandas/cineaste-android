package de.cineaste.android.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import de.cineaste.android.R
import de.cineaste.android.adapter.ResultAdapter
import de.cineaste.android.database.NearbyMessageHandler
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.entity.movie.MatchingResult
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.movie.MovieDto
import de.cineaste.android.entity.movie.NearbyMessage
import de.cineaste.android.network.NetworkCallback
import de.cineaste.android.network.NetworkClient
import de.cineaste.android.network.NetworkRequest
import de.cineaste.android.network.NetworkResponse
import de.cineaste.android.util.DateAwareGson
import de.cineaste.android.util.MultiList
import java.util.*

class ResultActivity : AppCompatActivity(), ResultAdapter.OnMovieSelectListener {

    private var nearbyMessages: List<NearbyMessage>? = null
    private var movieDbHelper: MovieDbHelper? = null

    private val results: ArrayList<MatchingResult>
        get() {
            val results = ArrayList<MatchingResult>()
            val multiList = MultiList()
            multiList.addAll(movies)

            for (multiListEntry in multiList.movieList) {
                results.add(MatchingResult(multiListEntry.movieDto, multiListEntry.counter))
            }

            return results
        }

    private val movies: ArrayList<MovieDto>
        get() {
            val movies = ArrayList<MovieDto>()

            for (current in nearbyMessages!!) {
                movies.addAll(current.movies)
            }

            return movies
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        nearbyMessages = NearbyMessageHandler.getMessages()

        movieDbHelper = MovieDbHelper.getInstance(this)

        initToolbar()

        val result = findViewById<RecyclerView>(R.id.result_list)

        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        result.layoutManager = llm
        result.itemAnimator = DefaultItemAnimator()

        val resultAdapter = ResultAdapter(
                results,
                this)
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

        val selectedMovieId = results[position].id
        val selectedMovie = movieDbHelper!!.readMovie(selectedMovieId)

        if (selectedMovie == null) {
            val client = NetworkClient(NetworkRequest(resources).getMovie(results[position].id))
            client.sendRequest(object : NetworkCallback {
                override fun onFailure() {

                }

                override fun onSuccess(response: NetworkResponse) {
                    val gson = DateAwareGson().gson
                    val movie = gson.fromJson(response.responseReader, Movie::class.java)
                    runOnUiThread { updateMovie(movie) }
                }
            })
        } else {
            updateMovie(selectedMovie)
        }
    }

    private fun updateMovie(movie: Movie) {
        movie.isWatched = true
        movieDbHelper!!.createOrUpdate(movie)
    }
}