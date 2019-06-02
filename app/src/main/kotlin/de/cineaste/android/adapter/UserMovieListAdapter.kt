package de.cineaste.android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.cineaste.android.R
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.viewholder.movie.MovieViewHolder

class UserMovieListAdapter constructor(
    private val dataSet: List<Movie>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.card_movie, parent, false)

        return MovieViewHolder(view, parent.context, null)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MovieViewHolder).assignData(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}