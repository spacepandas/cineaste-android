package de.cineaste.android.viewholder.movie

import android.content.Context
import android.view.View
import android.widget.Button

import de.cineaste.android.R
import de.cineaste.android.adapter.movie.MovieSearchQueryAdapter
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.listener.ItemClickListener

class MovieSearchViewHolder(
    itemView: View,
    context: Context,
    private val movieStateChange: MovieSearchQueryAdapter.OnMovieStateChange,
    listener: ItemClickListener
) : AbstractMovieViewHolder(itemView, context, listener) {

    private val addToWatchlistButton: Button = itemView.findViewById(R.id.to_watchlist_button)
    private val movieWatchedButton: Button = itemView.findViewById(R.id.history_button)

    override fun assignData(movie: Movie) {
        setBaseInformation(movie)

        addToWatchlistButton.setOnClickListener { v ->
            val index = this@MovieSearchViewHolder.adapterPosition
            movieStateChange.onMovieStateChangeListener(movie, v.id, index)
        }

        movieWatchedButton.setOnClickListener { v ->
            val index = this@MovieSearchViewHolder.adapterPosition
            movieStateChange.onMovieStateChangeListener(movie, v.id, index)
        }

        view.setOnClickListener { listener.onItemClickListener(movie.id, arrayOf(view, poster)) }
    }
}
