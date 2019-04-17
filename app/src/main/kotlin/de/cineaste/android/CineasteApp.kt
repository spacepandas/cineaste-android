package de.cineaste.android

import android.app.Application
import de.cineaste.android.database.CineasteDb
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.database.dbHelper.SeriesDbHelper
import de.cineaste.android.database.dbHelper.UserDbHelper

class CineasteApp : Application() {

    private lateinit var db: CineasteDb

    override fun onCreate() {
        super.onCreate()

         db = CineasteDb.getDatabase(this)
    }
}