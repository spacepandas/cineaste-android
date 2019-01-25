package de.cineaste.android.entity

import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.series.Series

data class ImportExportObject(
    var movies: List<Movie> = emptyList(),
    var series: List<Series> = emptyList()
)
