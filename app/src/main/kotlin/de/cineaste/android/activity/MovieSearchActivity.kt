package de.cineaste.android.activity

import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.View

import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type

import de.cineaste.android.R
import de.cineaste.android.adapter.movie.MovieSearchQueryAdapter
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.network.NetworkCallback
import de.cineaste.android.network.NetworkClient
import de.cineaste.android.network.NetworkRequest
import de.cineaste.android.network.NetworkResponse

class MovieSearchActivity : AbstractSearchActivity(), MovieSearchQueryAdapter.OnMovieStateChange {

    private val db = MovieDbHelper.getInstance(this)
    private lateinit var movieQueryAdapter: MovieSearchQueryAdapter

    override val layout: Int
        get() = R.layout.activity_search

    override val listAdapter: RecyclerView.Adapter<*>
        get() = movieQueryAdapter

    override val listType: Type
        get() = object : TypeToken<List<Movie>>() {

        }.type

    override fun getIntentForDetailActivity(itemId: Long): Intent {
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra(BaseDao.MovieEntry.ID, itemId)
        intent.putExtra(this.getString(R.string.state), R.string.searchState)
        return intent
    }

    override fun onMovieStateChangeListener(movie: Movie, viewId: Int, index: Int) {
        val callback: NetworkCallback?
        when (viewId) {
            R.id.to_watchlist_button -> callback = object : NetworkCallback {
                override fun onFailure() {
                    runOnUiThread { movieAddError(movie, index) }
                }

                override fun onSuccess(response: NetworkResponse) {
                    db.createOrUpdate(gson.fromJson(response.responseReader, Movie::class.java))

                }
            }
            R.id.history_button -> callback = object : NetworkCallback {
                override fun onFailure() {
                    runOnUiThread { movieAddError(movie, index) }
                }

                override fun onSuccess(response: NetworkResponse) {
                    val myMovie = gson.fromJson(response.responseReader, Movie::class.java)
                    myMovie.isWatched = true
                    db.createOrUpdate(myMovie)
                }
            }
            else -> callback = null
        }
        if (callback != null) {
            movieQueryAdapter.removeMovie(movie)
            val client = NetworkClient(NetworkRequest(resources).getMovie(movie.id))
            client.sendRequest(callback)
        }

    }

    private fun movieAddError(movie: Movie, index: Int) {
        val snackbar = Snackbar
                .make(recyclerView, R.string.could_not_add_movie, Snackbar.LENGTH_LONG)
        snackbar.show()
        movieQueryAdapter.addMovie(movie, index)
    }

    override fun initAdapter() {
        movieQueryAdapter = MovieSearchQueryAdapter(this, this)
    }

    override fun getSuggestions() {
        val client = NetworkClient(NetworkRequest(resources).upcomingMovies)
        client.sendRequest(networkCallback)
    }

    override fun searchRequest(searchQuery: String) {
        val client = NetworkClient(NetworkRequest(resources).searchMovie(searchQuery))
        client.sendRequest(networkCallback)
    }

    override fun getRunnable(json: String, listType: Type): Runnable {
        return Runnable {
            val movies: List<Movie> = gson.fromJson(json, listType)
            movieQueryAdapter.addMovies(movies)
            progressBar.visibility = View.GONE
        }
    }
}
