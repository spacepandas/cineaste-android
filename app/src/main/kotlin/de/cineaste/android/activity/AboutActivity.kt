package de.cineaste.android.activity

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.webkit.JavascriptInterface
import android.webkit.WebView
import de.cineaste.android.BuildConfig
import de.cineaste.android.R

class AboutActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initToolbar()

        val webView = findViewById<WebView>(R.id.webview)
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebInterface(resources), "Android")
        webView.loadUrl("file:///android_res/raw/about.html")
    }


    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setTitle(R.string.about)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}

class WebInterface(private val resources: Resources) {

    @Suppress("unused")
    @JavascriptInterface
    fun addVersion(): String {
        return resources.getString(R.string.version, BuildConfig.VERSION_NAME)
    }
}
