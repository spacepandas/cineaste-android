package de.cineaste.android.entity

import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.series.Series

data class ImportExportObject (
        var movies: List<Movie> = emptyList(),
        var isMoviesSuccessfullyImported: Boolean = true,
        var series : List<Series> = emptyList(),
        var isSeriesSuccessfullyImported: Boolean = true
) {
    val isSuccessfullyImported: Boolean
        get() = isMoviesSuccessfullyImported && isSeriesSuccessfullyImported
}
