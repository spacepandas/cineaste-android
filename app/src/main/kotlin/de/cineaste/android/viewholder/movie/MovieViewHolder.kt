package de.cineaste.android.viewholder.movie

import android.content.Context
import android.graphics.Color
import android.view.View
import de.cineaste.android.R
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.listener.ItemClickListener

class MovieViewHolder(itemView: View, context: Context, listener: ItemClickListener?) :
    AbstractMovieViewHolder(itemView, context, listener) {

    override fun assignData(movie: Movie) {
        setBaseInformation(movie)

        movieRuntime.text = resources.getString(R.string.runtime, movie.runtime)
        listener?.let {
            view.setOnClickListener {
                listener.onItemClickListener(
                    movie.id,
                    arrayOf(view, poster, title, movieRuntime, movieVote)
                )
            }
        }
    }

    fun onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY)
    }

    fun onItemClear() {
        itemView.setBackgroundColor(Color.WHITE)
    }
}
