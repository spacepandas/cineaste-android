package de.cineaste.android.entity;


import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.entity.series.Series;

public class ImportExportObject {

    private List<Movie> movies;
    private List<Series> series;
    private List<Episode> episodes;

    public ImportExportObject() {
        this.movies = new ArrayList<>();
        this.series = new ArrayList<>();
        this.episodes = new ArrayList<>();
    }

    public ImportExportObject(List<Movie> movies, List<Series> series, List<Episode> episodes) {
        this.movies = movies;
        this.series = series;
        this.episodes = episodes;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }
}
