package de.cineaste.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // it's important _not_ to inflate a layout file here
        // because that would happen after the app is fully
        // initialized what is too late

        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()
    }
}
