package de.cineaste.android.adapter.movie

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.cineaste.android.R
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.viewholder.movie.MovieSearchViewHolder
import java.util.*

class MovieSearchQueryAdapter(private val listener: ItemClickListener, private val movieStateChange: OnMovieStateChange) : RecyclerView.Adapter<MovieSearchViewHolder>() {
    private val dataSet = ArrayList<Movie>()

    interface OnMovieStateChange {
        fun onMovieStateChangeListener(movie: Movie, viewId: Int, index: Int)
    }

    fun addMovies(movies: List<Movie>) {
        dataSet.clear()
        dataSet.addAll(movies)
        notifyDataSetChanged()
    }

    fun addMovie(movie: Movie, index: Int) {
        dataSet.add(index, movie)
    }

    fun removeMovie(index: Int) {
        dataSet.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSearchViewHolder {
        val v = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.card_movie_search, parent, false)
        return MovieSearchViewHolder(v, parent.context, movieStateChange, listener)
    }

    override fun onBindViewHolder(holder: MovieSearchViewHolder, position: Int) {
        holder.assignData(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}

