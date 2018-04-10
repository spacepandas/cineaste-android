package de.cineaste.android.database

import android.os.Environment
import com.google.gson.Gson
import de.cineaste.android.database.ImportExportService.Companion.FOLDER_NAME
import de.cineaste.android.database.ImportExportService.Companion.MOVIES_FILE
import de.cineaste.android.database.ImportExportService.Companion.SERIES_FILE
import de.cineaste.android.entity.ImportExportObject
import de.cineaste.android.entity.movie.Movie
import de.cineaste.android.entity.series.Series
import java.io.File
import java.io.FileWriter
import java.io.IOException

object ExportService {

    private val gson = Gson()

    fun export(importExportObject: ImportExportObject): ImportExportObject {
        val exportPath = File(Environment.getExternalStorageDirectory().toString() + "/" + FOLDER_NAME)

        if (!exportPath.exists()) {
            if (exportPath.mkdir())
                return importExportObject
        }

        importExportObject.isMoviesSuccessfullyImported = exportMovies(importExportObject.movies)
        importExportObject.isSeriesSuccessfullyImported = exportSeries(importExportObject.series)

        return importExportObject
    }


    private fun exportMovies(movies: List<Movie>): Boolean {
        return writeOnDevice(getFile(MOVIES_FILE), gson.toJson(movies))
    }

    private fun exportSeries(series: List<Series>): Boolean {
        return writeOnDevice(getFile(SERIES_FILE), gson.toJson(series))
    }

    fun getFile(fileName: String): File {
        return File(
                Environment.getExternalStorageDirectory().toString() + "/" + FOLDER_NAME + "/" + fileName)
    }

    fun writeOnDevice(exportFile: File, json: String): Boolean {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(exportFile)
            fileWriter.write(json)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            try {
                if (fileWriter != null)
                    fileWriter.close()
            } catch (e: Exception) {
                //die silently
            }

        }
    }
}
