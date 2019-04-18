package de.cineaste.android

import android.app.Application
import de.cineaste.android.database.CineasteDb
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var db: CineasteDb? = null

class CineasteApp : Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch {
            db = CineasteDb.getDatabase(applicationContext)
        }
    }
}