package de.cineaste.android.viewholder.movie

import android.content.Context
import android.view.View
import android.widget.TextView

import de.cineaste.android.R
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.listener.ItemClickListener
import de.cineaste.android.viewholder.BaseViewHolder

abstract class AbstractMovieViewHolder(
    itemView: View,
    context: Context,
    listener: ItemClickListener?
) :
    BaseViewHolder(itemView, listener, context) {

    private val releaseDate: TextView = itemView.findViewById(R.id.releaseDate)
    val movieVote: TextView = itemView.findViewById(R.id.vote)
    lateinit var movieRuntime: TextView

    init {
        try {
            movieRuntime = itemView.findViewById(R.id.movieRuntime)
        } catch (ex: Exception) {
        }
    }

    abstract fun assignData(movie: Movie)

    fun setBaseInformation(movie: Movie) {
        title.text = movie.title
        val movieReleaseDate = movie.releaseDate

        movieVote.text = resources.getString(R.string.vote, movie.voteAverage.toString())

        if (movieReleaseDate != null) {
            releaseDate.text = convertDate(movieReleaseDate)
        } else {
            releaseDate.text = resources.getString(R.string.coming_soon)
        }
        setPoster(movie.posterPath)
    }
}
