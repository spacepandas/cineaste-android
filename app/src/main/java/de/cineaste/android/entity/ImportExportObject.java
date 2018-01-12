package de.cineaste.android.entity;


import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.entity.series.Series;

public class ImportExportObject {

    private List<Movie> movies = new ArrayList<>();
    private boolean moviesSuccessfullyImported = true;
    private List<Series> series = new ArrayList<>();
    private boolean seriesSuccessfullyImported = true;
    private List<Episode> episodes = new ArrayList<>();
    private boolean episodesSuccessfullyImported = true;

    public ImportExportObject() {
    }

    public boolean isSuccessfullyImported() {
        return moviesSuccessfullyImported && seriesSuccessfullyImported && episodesSuccessfullyImported;
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

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes.clear();
        this.episodes.addAll(episodes);
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

    public boolean isEpisodesSuccessfullyImported() {
        return episodesSuccessfullyImported;
    }

    public void setEpisodesSuccessfullyImported(boolean episodesSuccessfullyImported) {
        this.episodesSuccessfullyImported = episodesSuccessfullyImported;
    }
}
