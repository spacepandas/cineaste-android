package de.cineaste.android.activity

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import de.cineaste.android.R
import de.cineaste.android.adapter.movie.MovieSearchQueryAdapter
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.network.NetworkClient
import de.cineaste.android.network.NetworkRequest
import java.lang.reflect.Type

class MovieSearchActivity : AbstractSearchActivity() {

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

    override fun initAdapter() {
        movieQueryAdapter = MovieSearchQueryAdapter(this)
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
