package de.cineaste.android.database;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.cineaste.android.entity.ImportExportObject;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.entity.series.Series;

import static de.cineaste.android.database.ImportExportService.FOLDER_NAME;
import static de.cineaste.android.database.ImportExportService.MOVIES_FILE;
import static de.cineaste.android.database.ImportExportService.SERIES_FILE;

public class ExportService {

    private static final Gson gson = new Gson();

    public static ImportExportObject export(ImportExportObject importExportObject) {
        File exportPath = new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME);

        if (!exportPath.exists()) {
            if (exportPath.mkdir())
                return importExportObject;
        }

        importExportObject.setMoviesSuccessfullyImported(exportMovies(importExportObject.getMovies()));
        importExportObject.setSeriesSuccessfullyImported(exportSeries(importExportObject.getSeries()));

        return importExportObject;
    }


    private static boolean exportMovies(List<Movie> movies) {
        return writeOnDevice(getFile(MOVIES_FILE), gson.toJson(movies));
    }

    private static boolean exportSeries(List<Series> series) {
        return writeOnDevice(getFile(SERIES_FILE), gson.toJson(series));
    }

    @NonNull
    private static File getFile(String fileName) {
        return new File(
                Environment.getExternalStorageDirectory() + "/" + FOLDER_NAME + "/" + fileName);
    }

    private static boolean writeOnDevice(File exportFile, String json) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(exportFile);
            fileWriter.write(json);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close();
            } catch (Exception e) {
                //die silently
            }
        }
    }


}
