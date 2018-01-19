package de.cineaste.android.entity;


import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.entity.series.Series;

public class ImportExportObject {

    private List<Movie> movies = new ArrayList<>();
    private boolean moviesSuccessfullyImported = true;
    private List<Series> series = new ArrayList<>();
    private boolean seriesSuccessfullyImported = true;

    public ImportExportObject() {
    }

    public boolean isSuccessfullyImported() {
        return moviesSuccessfullyImported && seriesSuccessfullyImported;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series.clear();
        this.series.addAll(series);
    }


    public boolean isMoviesSuccessfullyImported() {
        return moviesSuccessfullyImported;
    }

    public void setMoviesSuccessfullyImported(boolean moviesSuccessfullyImported) {
        this.moviesSuccessfullyImported = moviesSuccessfullyImported;
    }

    public boolean isSeriesSuccessfullyImported() {
        return seriesSuccessfullyImported;
    }

    public void setSeriesSuccessfullyImported(boolean seriesSuccessfullyImported) {
        this.seriesSuccessfullyImported = seriesSuccessfullyImported;
    }

}
