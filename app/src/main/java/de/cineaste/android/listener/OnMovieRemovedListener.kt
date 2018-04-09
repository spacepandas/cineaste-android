package de.cineaste.android.listener

import de.cineaste.android.entity.movie.Movie

interface OnMovieRemovedListener {

    fun removeMovie(movie: Movie)
}
