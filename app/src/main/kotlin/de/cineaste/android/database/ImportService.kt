package de.cineaste.android.database

import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.cineaste.android.database.ImportExportService.Companion.FOLDER_NAME
import de.cineaste.android.database.ImportExportService.Companion.MOVIES_FILE
import de.cineaste.android.database.ImportExportService.Companion.SERIES_FILE
import de.cineaste.android.entity.ImportExportObject
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.series.Series
import java.io.File
import java.io.IOException
import java.util.*

object ImportService {

    private val gson = Gson()

    fun importFiles(): ImportExportObject {
        val importExportObject = ImportExportObject()
        val movies = ArrayList<Movie>()
        val series = ArrayList<Series>()

        try {
            movies.addAll(importMovies())
        } catch (ex: IOException) {
            importExportObject.isMoviesSuccessfullyImported = false
        }

        try {
            series.addAll(importSeries())
        } catch (ex: IOException) {
            importExportObject.isSeriesSuccessfullyImported = false
        }

        importExportObject.movies = movies
        importExportObject.series = series

        return importExportObject
    }

    @Throws(IOException::class)
    private fun importMovies(): List<Movie> {
        val importedMoviesString = readJsonFromFile(MOVIES_FILE)

        val listType = object : TypeToken<List<Movie>>() {

        }.type
        return gson.fromJson(importedMoviesString, listType)
    }

    @Throws(IOException::class)
    private fun importSeries(): List<Series> {
        val importedSeriesString = readJsonFromFile(SERIES_FILE)

        val listType = object : TypeToken<List<Series>>() {

        }.type
        return gson.fromJson(importedSeriesString, listType)
    }

    @Throws(IOException::class)
    fun readJsonFromFile(fileName: String): String {
        val importFile = File(
                Environment.getExternalStorageDirectory().toString() + "/" + FOLDER_NAME + "/" + fileName)

        val temp:List<String> = importFile.bufferedReader().readLines()
        val text = StringBuilder()

        for (line in temp) {
            text.append(line)
        }

        return text.toString()
    }
}
