package de.cineaste.android.listener;

import de.cineaste.android.entity.movie.Movie;

public interface OnMovieRemovedListener {

    @SuppressWarnings("unused")
    void removeMovie(Movie movie);
}
