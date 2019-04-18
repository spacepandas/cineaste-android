package de.cineaste.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // it's important _not_ to inflate a layout file here
        // because that would happen after the app is fully
        // initialized what is too late
        checkDb()
        handler.removeCallbacks(callback)
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }

    private fun checkDb() {
        handler.postDelayed(
            callback, 500
        )
    }

    val callback = Runnable {
        if (db == null) {
            checkDb()
        }
    }

    companion object {

    }
}
