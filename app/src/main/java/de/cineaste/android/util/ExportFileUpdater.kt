package de.cineaste.android.util

import android.os.AsyncTask
import android.util.Log

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import de.cineaste.android.database.ExportService
import de.cineaste.android.database.ImportService

import de.cineaste.android.database.ImportExportService.MOVIES_FILE

object ExportFileUpdater {

    fun updateFile() {
        //todo use coroutines
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                try {
                    var jsonString = ImportService.readJsonFromFile(MOVIES_FILE)
                    jsonString = updateDateTypes(jsonString)

                    ExportService.writeOnDevice(ExportService.getFile(MOVIES_FILE), jsonString)
                } catch (ex: Exception) {
                    Log.d("Cineaste", "Update Export-file went wrong")
                }

                return null
            }
        }.execute()
    }

    @Throws(JSONException::class)
    private fun updateDateTypes(jsonString: String): String {
        val jsonArray = JSONArray(jsonString)

        for (i in 0 until jsonArray.length()) {
            val movie = jsonArray.getJSONObject(i)
            changeWatchedDateFromLongToDate(movie)
        }
        return jsonArray.toString()
    }

    @Throws(JSONException::class)
    private fun changeWatchedDateFromLongToDate(movie: JSONObject) {
        val date = movie.getLong("watchedDate")
        movie.remove("watchedDate")
        movie.accumulate("watchedDate", dateLongToString(date))
    }

    private fun dateLongToString(date: Long): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH)

        return dateFormat.format(Date(date))
    }
}
