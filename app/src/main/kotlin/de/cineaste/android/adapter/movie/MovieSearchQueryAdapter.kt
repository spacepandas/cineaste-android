package de.cineaste.android.adapter.movie

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.cineaste.android.R
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.viewholder.movie.MovieSearchViewHolder

class MovieSearchQueryAdapter(
    private val listener: ItemClickListener
) : RecyclerView.Adapter<MovieSearchViewHolder>() {
    private val dataSet = ArrayList<Movie>()

    fun addMovies(movies: List<Movie>) {
        dataSet.clear()
        dataSet.addAll(movies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSearchViewHolder {
        val v = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_search, parent, false)
        return MovieSearchViewHolder(v, parent.context, listener)
    }

    override fun onBindViewHolder(holder: MovieSearchViewHolder, position: Int) {
        holder.assignData(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}
