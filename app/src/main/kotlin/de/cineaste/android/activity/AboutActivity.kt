package de.cineaste.android.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.cineaste.android.BuildConfig
import de.cineaste.android.R

class AboutActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initToolbar()

        val movieDbLogo = findViewById<ImageView>(R.id.themoviedb_logo)

        movieDbLogo.setOnClickListener(this)

        val version = findViewById<TextView>(R.id.version)
        version.text = resources.getString(R.string.version, BuildConfig.VERSION_NAME)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setTitle(R.string.about_open_source)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.themoviedb_logo -> openWebsite(MOVIE_DB_URL)
        }
    }

    companion object {
        private const val MOVIE_DB_URL = "https://www.themoviedb.org/"
    }
}
