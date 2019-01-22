package de.cineaste.android.database

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import de.cineaste.android.entity.ImportExportObject
import java.io.FileOutputStream

object ExportService {

    private val gson = Gson()

    fun export(importExportObject: ImportExportObject, uri: Uri, context: Context): Boolean {

        return try {
            writeOnDevice(importExportObject, uri, context)
            true
        } catch (ex: Exception) {
            false
        }
    }

    private fun writeOnDevice(importExportObject: ImportExportObject, uri: Uri, context: Context) {
        val pfd = context.contentResolver.openFileDescriptor(uri, "w")
        pfd?.let {
            val fileOutputStream = FileOutputStream(it.fileDescriptor)
            fileOutputStream.write(gson.toJson(importExportObject).toByteArray())
            fileOutputStream.close()
            it.close()
        }
    }
}
