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
    listener: ItemClickListener
) :
    BaseViewHolder(itemView, listener, context) {

    private val movieReleaseDate: TextView = itemView.findViewById(R.id.movieReleaseDate)
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
        val releaseDate = movie.releaseDate

        if (releaseDate != null) {
            movieReleaseDate.text = convertDate(releaseDate)
            movieReleaseDate.visibility = View.VISIBLE
        } else {
            movieReleaseDate.visibility = View.GONE
        }
        setPoster(movie.posterPath)
    }
}
