package de.cineaste.android.database;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.ImportExportObject;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.entity.series.Series;

import static de.cineaste.android.database.ImportExportService.EPISODES_FILE;
import static de.cineaste.android.database.ImportExportService.FOLDER_NAME;
import static de.cineaste.android.database.ImportExportService.MOVIES_FILE;
import static de.cineaste.android.database.ImportExportService.SERIES_FILE;

public class ImportService {

    private static final Gson gson = new Gson();

    public static ImportExportObject importFiles() {
        ImportExportObject importExportObject = new ImportExportObject();
        List<Movie> movies = new ArrayList<>();
        List<Series> series = new ArrayList<>();
        List<Episode> episodes = new ArrayList<>();

        try {
            movies.addAll(importMovies());
        } catch (IOException ex) {
            importExportObject.setMoviesSuccessfullyImported(false);
        }

        try {
            series.addAll(importSeries());
        } catch (IOException ex) {
            importExportObject.setSeriesSuccessfullyImported(false);
        }

        try {
            episodes.addAll(importEpisodes());
        } catch (IOException ex) {
            importExportObject.setEpisodesSuccessfullyImported(false);
        }

        importExportObject.setMovies(movies);
        importExportObject.setSeries(series);
        importExportObject.setEpisodes(episodes);

        return importExportObject;
    }

    private static List<Movie> importMovies() throws IOException {
        String importedMoviesString = readJsonFromFile(MOVIES_FILE);

        Type listType = new TypeToken<List<Movie>>() {
        }.getType();
        return gson.fromJson(importedMoviesString, listType);
    }

    private static List<Series> importSeries() throws IOException {
        String importedSeriesString = readJsonFromFile(SERIES_FILE);

        Type listType = new TypeToken<List<Series>>() {
        }.getType();
        return gson.fromJson(importedSeriesString, listType);
    }

    private static List<Episode> importEpisodes() throws IOException {
        String importedEpisodesString = readJsonFromFile(EPISODES_FILE);

        Type listType = new TypeToken<List<Episode>>() {
        }.getType();
        return gson.fromJson(importedEpisodesString, listType);
    }

    private static String readJsonFromFile(String fileName) throws IOException {
        final File importFile = new File(
                Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/" + fileName);

        FileReader fileReader = new FileReader(importFile);
        BufferedReader reader = new BufferedReader(fileReader);

        String temp;
        StringBuilder text = new StringBuilder();
        while ((temp = reader.readLine()) != null) {
            text.append(temp);
        }

        return text.toString();
    }
}
