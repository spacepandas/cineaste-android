package de.cineaste.android.adapter.movie

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.Filter
import de.cineaste.android.R
import de.cineaste.android.adapter.BaseListAdapter
import de.cineaste.android.database.dbHelper.NMovieDbHelper
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.fragment.WatchState
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.listener.OnMovieRemovedListener
import de.cineaste.android.viewholder.movie.MovieViewHolder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue
import java.util.LinkedList
import java.util.Collections

class MovieListAdapter(displayMessage: BaseListAdapter.DisplayMessage, context: Context, listener: ItemClickListener, state: WatchState) : BaseListAdapter(context, displayMessage, listener, state), OnMovieRemovedListener {

    private val db: NMovieDbHelper = NMovieDbHelper.getInstance(context)
    private var dataSet: MutableList<Movie> = ArrayList()
    private var filteredDataSet: MutableList<Movie> = mutableListOf()

    private val updatedMovies = LinkedBlockingQueue<UpdatedMovies>()

    override val internalFilter: Filter
        get() = FilterMovies(this, dataSet)

    val dataSetSize: Int
        get() = dataSet.size

    override val layout: Int
        get() = R.layout.card_movie

    init {
        this.dataSet.clear()
        var movies = listOf<Movie>()
        GlobalScope.launch { movies = db.readMoviesByWatchStatus(state) }
        this.dataSet.addAll(movies)
        this.filteredDataSet = LinkedList(dataSet)
    }

    fun removeItem(position: Int) {
        val movie = filteredDataSet[position]
        GlobalScope.launch { db.deleteMovieFromWatchlist(movie) }
        removeMovie(movie)
    }

    fun getItem(position: Int): Movie {
        return filteredDataSet[position]
    }

    fun restoreDeletedItem(item: Movie, position: Int) {
        GlobalScope.launch { db.createOrUpdate(item) }
        filteredDataSet.add(position, item)
        dataSet.add(position, item)
        notifyItemInserted(position)
    }

    fun toggleItemOnList(item: Movie) {
        item.isWatched = !item.isWatched
        GlobalScope.launch { db.createOrUpdate(item) }
        removeMovie(item)
    }

    fun restoreToggleItemOnList(item: Movie, position: Int) {
        item.isWatched = !item.isWatched
        GlobalScope.launch { db.createOrUpdate(item) }
        filteredDataSet.add(position, item)
        dataSet.add(position, item)
        notifyItemInserted(position)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        updateMoviePositionsAndAddToQueue(fromPosition, toPosition)

        Collections.swap(filteredDataSet, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    private fun updateMoviePositionsAndAddToQueue(fromPosition: Int, toPosition: Int) {
        val passiveMovedMovie = filteredDataSet.removeAt(toPosition)
        passiveMovedMovie.listPosition = fromPosition
        filteredDataSet.add(toPosition, passiveMovedMovie)

        val prev = filteredDataSet.removeAt(fromPosition)
        prev.listPosition = toPosition
        filteredDataSet.add(fromPosition, prev)
        updatedMovies.add(UpdatedMovies(prev, passiveMovedMovie))
    }

    fun orderAlphabetical() {
        var movies = listOf<Movie>()
        GlobalScope.launch { movies = db.reorderAlphabetical(state) }
        dataSet = movies as MutableList<Movie>
        filteredDataSet = movies as MutableList<Movie>
    }

    fun orderByReleaseDate() {
        var movies = listOf<Movie>()
        GlobalScope.launch { movies = db.reorderByReleaseDate(state) }
        dataSet = movies as MutableList<Movie>
        filteredDataSet = movies as MutableList<Movie>
    }

    fun orderByRuntime() {
        var movies = listOf<Movie>()
        GlobalScope.launch { movies = db.reorderByRuntime(state) }
        dataSet = movies as MutableList<Movie>
        filteredDataSet = movies as MutableList<Movie>
    }

    fun updatePositionsInDb() {
        while (updatedMovies.iterator().hasNext()) {
            val movies = updatedMovies.poll()
            GlobalScope.launch {
                db.updatePosition(movies.prev)
                db.updatePosition(movies.passiveMovie)
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredDataSet.size
    }

    override fun createViewHolder(v: View): RecyclerView.ViewHolder {
        return MovieViewHolder(v, context, listener)
    }

    override fun assignDataToViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieViewHolder).assignData(filteredDataSet[position])
    }

    override fun removeMovie(movie: Movie) {
        notifyItemRemoved(filteredDataSet.indexOf(movie))

        dataSet.remove(movie)
        filteredDataSet.remove(movie)
        displayMessage.showMessageIfEmptyList()
    }

    fun updateDataSet() {
        var movies = listOf<Movie>()
        GlobalScope.launch { movies = db.readMoviesByWatchStatus(state) }
        this.dataSet = movies as MutableList<Movie>
        this.filteredDataSet = LinkedList(dataSet)
        displayMessage.showMessageIfEmptyList()
        notifyDataSetChanged()
    }

    inner class FilterMovies internal constructor(private val adapter: MovieListAdapter, private val movieList: List<Movie>) : Filter() {
        private val filteredMovieList: MutableList<Movie>

        init {
            this.filteredMovieList = ArrayList()
        }

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            filteredMovieList.clear()
            val results = Filter.FilterResults()

            if (constraint == null || constraint.isEmpty()) {
                filteredMovieList.addAll(movieList)
            } else {
                val filterPattern = constraint.toString().toLowerCase().trim { it <= ' ' }

                for (movie in movieList) {
                    if (movie.title.toLowerCase().contains(filterPattern)) {
                        filteredMovieList.add(movie)
                    }
                }
            }

            results.values = filteredMovieList
            results.count = filteredMovieList.size

            return results
        }

        override fun publishResults(charSequence: CharSequence, results: Filter.FilterResults) {
            adapter.filteredDataSet.clear()

            adapter.filteredDataSet.addAll(results.values as List<Movie>)
            adapter.notifyDataSetChanged()
        }
    }

    inner class UpdatedMovies internal constructor(internal val prev: Movie, internal val passiveMovie: Movie)
}