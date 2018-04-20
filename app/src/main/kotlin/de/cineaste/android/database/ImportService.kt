package de.cineaste.android.database

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import de.cineaste.android.entity.ImportExportObject
import java.io.FileInputStream
import java.io.IOException

object ImportService {

    private val gson = Gson()

    fun importFiles(uri: Uri, context: Context): ImportExportObject {
        return try {
            gson.fromJson(readJsonFromUri(uri, context), ImportExportObject::class.java)
        } catch (ex: Exception) {
            ImportExportObject()
        }
    }

    @Throws(IOException::class)
    private fun readJsonFromUri(uri: Uri, context: Context): String {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor.fileDescriptor
        val fis = FileInputStream(fileDescriptor)

        val stringBuilder = StringBuilder()
        val line = fis.bufferedReader().readLines()

        for (s in line) {
            stringBuilder.append(s)
        }

        parcelFileDescriptor.close()
        fis.close()

        return stringBuilder.toString()
    }
}
