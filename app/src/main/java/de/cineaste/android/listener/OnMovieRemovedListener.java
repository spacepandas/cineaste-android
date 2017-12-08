package de.cineaste.android.listener;

import de.cineaste.android.entity.Movie;

public interface OnMovieRemovedListener {

    @SuppressWarnings("unused")
    void removeMovie(Movie movie);
}
