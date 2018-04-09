package de.cineaste.android.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.cineaste.android.database.ExportService;
import de.cineaste.android.database.ImportService;

import static de.cineaste.android.database.ImportExportService.MOVIES_FILE;

public class ExportFileUpdater {

    public static void updateFile() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String jsonString = ImportService.readJsonFromFile(MOVIES_FILE);
                    jsonString = updateDateTypes(jsonString);

                    ExportService.writeOnDevice(ExportService.getFile(MOVIES_FILE), jsonString);
                } catch (Exception ex) {
                    Log.d("Cineaste",  "Update Export-file went wrong");
                }

                return null;
            }
        }.execute();
    }

    @NonNull
    private static String updateDateTypes(String jsonString) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movie = jsonArray.getJSONObject(i);
            changeWatchedDateFromLongToDate(movie);
        }
        return jsonArray.toString();
    }

    private static void changeWatchedDateFromLongToDate(JSONObject movie) throws JSONException {
        long date = movie.getLong("watchedDate");
        movie.remove("watchedDate");
        movie.accumulate("watchedDate", dateLongToString(date));
    }

    private static String dateLongToString(long date) {
        final DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);

        return dateFormat.format(new Date(date));
    }
}
