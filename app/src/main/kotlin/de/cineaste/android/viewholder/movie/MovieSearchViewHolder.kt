package de.cineaste.android.viewholder.movie

import android.content.Context
import android.view.View
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.listener.ItemClickListener

class MovieSearchViewHolder(
    itemView: View,
    context: Context,
    listener: ItemClickListener
) : AbstractMovieViewHolder(itemView, context, listener) {

    override fun assignData(movie: Movie) {
        setBaseInformation(movie)

        listener?.let {
            view.setOnClickListener {
                listener.onItemClickListener(
                    movie.id,
                    arrayOf(view, poster)
                )
            }
        }
    }
}
