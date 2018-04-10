package de.cineaste.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import de.cineaste.android.activity.AboutActivity
import de.cineaste.android.activity.MovieNightActivity
import de.cineaste.android.database.ExportService
import de.cineaste.android.database.ImportService
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.database.dbHelper.SeriesDbHelper
import de.cineaste.android.database.dbHelper.UserDbHelper
import de.cineaste.android.entity.ImportExportObject
import de.cineaste.android.entity.User
import de.cineaste.android.fragment.*
import de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.Companion.MOVIE_COUNT
import de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.Companion.SERIES_COUNT
import de.cineaste.android.util.ExportFileUpdater
import java.util.*

class MainActivity : AppCompatActivity(), UserInputFragment.UserNameListener {

    private var fm: FragmentManager? = null
    private var contentContainer: View? = null
    private var userDbHelper: UserDbHelper? = null
    private var userName: TextView? = null

    private var drawerLayout: DrawerLayout? = null

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            drawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            if (fm!!.backStackEntryCount > 1)
                super.onBackPressed()
            else
                finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (fm!!.backStackEntryCount > 1)
            fm!!.popBackStack()
        else
            drawerLayout!!.openDrawer(GravityCompat.START)

        return false
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                        this,
                        R.string.missing_permission,
                        Toast.LENGTH_SHORT).show()
            }
            else -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ExportFileUpdater.updateFile()

        userDbHelper = UserDbHelper.getInstance(this)
        movieDbHelper = MovieDbHelper.getInstance(this)
        seriesDbHelper = SeriesDbHelper.getInstance(this)
        contentContainer = findViewById(R.id.content_container)

        fm = supportFragmentManager

        initToolbar()
        initNavDrawer()

        checkPermissions()

        if (savedInstanceState == null) {
            replaceFragment(fm!!, getBaseWatchlistFragment(WatchState.WATCH_STATE))
        }
    }

    override fun onResume() {
        super.onResume()

        val user = userDbHelper!!.user
        if (user != null && userName != null) {
            userName!!.text = user.userName
        }
    }

    private fun replaceFragment(fm: FragmentManager, fragment: Fragment) {
        fm.beginTransaction()
                .replace(
                        R.id.content_container,
                        fragment, fragment.javaClass.name)
                .addToBackStack(null)
                .commit()
    }

    private fun replaceFragmentPopBackStack(fm: FragmentManager, fragment: Fragment) {
        fm.popBackStack()
        replaceFragment(fm, fragment)
    }

    private fun checkPermissions() {
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()
        for (p in permissions) {
            val result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), 1)
        }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initNavDrawer() {
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(CustomDrawerClickListener())

        colorMenu(navigationView.menu)

        userName = navigationView.getHeaderView(0).findViewById(R.id.username)

        drawerLayout = findViewById(R.id.drawer_layout)
        val drawerToggle = ActionBarDrawerToggle(
                this, drawerLayout, R.string.open, R.string.close
        )
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    private fun colorMenu(menu: Menu) {
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)

            if (menuItem.title != null) {
                val spanString = SpannableString(menuItem.title.toString())
                spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.toolbar_text)), 0, spanString.length, 0)
                menuItem.title = spanString
            }

            val drawable = menuItem.icon
            if (drawable != null) {
                drawable.mutate()
                drawable.setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
            }

            val subMenu = menuItem.subMenu
            if (subMenu != null) {
                colorMenu(subMenu)
            }
        }
    }

    private inner class CustomDrawerClickListener : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.show_movie_watchlist -> {
                    val watchlistFragment = getBaseWatchlistFragment(WatchState.WATCH_STATE)
                    replaceFragmentPopBackStack(fm!!, watchlistFragment)
                }
                R.id.show_movie_watchedlist -> {
                    val watchedlistFragment = getBaseWatchlistFragment(WatchState.WATCHED_STATE)
                    replaceFragmentPopBackStack(fm!!, watchedlistFragment)
                }
                R.id.show_series_watchlist -> {
                    val seriesWatchlistFragment = getSeriesListFragment(WatchState.WATCH_STATE)
                    replaceFragmentPopBackStack(fm!!, seriesWatchlistFragment)
                }
                R.id.show_series_watchedlist -> {
                    val seriesWatchedlistFragment = getSeriesListFragment(WatchState.WATCHED_STATE)
                    replaceFragmentPopBackStack(fm!!, seriesWatchedlistFragment)
                }
                R.id.exportMovies -> exportMovies()
                R.id.importMovies -> importMovies()
                R.id.about -> {
                    val intent = Intent(this@MainActivity, AboutActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout!!.closeDrawer(GravityCompat.START)
            return true
        }
    }

    private fun exportMovies() {
        var importExportObject = ImportExportObject()
        importExportObject.movies = movieDbHelper!!.readAllMovies()
        importExportObject.series = seriesDbHelper!!.allSeries
        importExportObject = ExportService.export(importExportObject)

        var snackBarMessage = R.string.exportFailed

        if (importExportObject.isSuccessfullyImported) {
            snackBarMessage = R.string.exportSucceeded
        }

        val snackbar = Snackbar
                .make(contentContainer!!, snackBarMessage, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    private fun importMovies() {
        var baseListFragment: BaseListFragment?
        baseListFragment = try {
            fm!!.findFragmentByTag(BaseMovieListFragment::class.java.name) as BaseListFragment
        } catch (ex: Exception) {
            null
        }

        if (baseListFragment == null) {
            baseListFragment = try {
                fm!!.findFragmentByTag(SeriesListFragment::class.java.name) as BaseListFragment
            } catch (ex: Exception) {
                null
            }

        }

        if (baseListFragment == null) {
            return
        }
        baseListFragment.progressbar!!.visibility = View.VISIBLE
        AsyncImporter().execute(AsyncInputAttribute(supportFragmentManager, baseListFragment))
    }

    private fun getBaseWatchlistFragment(state: WatchState): BaseMovieListFragment {
        val watchlistFragment = BaseMovieListFragment()
        val bundle = Bundle()
        bundle.putString(
                WatchState.WATCH_STATE_TYPE.name,
                state.name)
        watchlistFragment.arguments = bundle
        return watchlistFragment
    }

    private fun getSeriesListFragment(state: WatchState): SeriesListFragment {
        val seriesListFragment = SeriesListFragment()
        val bundle = Bundle()
        bundle.putString(
                WatchState.WATCH_STATE_TYPE.name,
                state.name)
        seriesListFragment.arguments = bundle
        return seriesListFragment
    }

    override fun onFinishUserDialog(userName: String) {
        if (!userName.isEmpty()) {
            userDbHelper!!.createUser(User(userName))
        }

        val intent = Intent(this@MainActivity, MovieNightActivity::class.java)
        startActivity(intent)
    }


    private class AsyncImporter : AsyncTask<AsyncInputAttribute, Void, AsyncOutputAttributes>() {

        override fun doInBackground(vararg asyncInputAttributes: AsyncInputAttribute): AsyncOutputAttributes {
            val importExportObject = ImportService.importFiles()

            //todo find a better solution to save all files
            for (movie in importExportObject.movies) {
                movieDbHelper!!.createOrUpdate(movie)
            }

            for (series in importExportObject.series) {
                seriesDbHelper!!.importSeries(series)
            }

            return AsyncOutputAttributes(importExportObject, asyncInputAttributes[0])
        }

        override fun onPostExecute(asyncOutputAttributes: AsyncOutputAttributes) {
            super.onPostExecute(asyncOutputAttributes)

            val importExportObject = asyncOutputAttributes.importExportObject

            val fragment = asyncOutputAttributes.asyncInputAttribute.baseListFragment
            fragment.progressbar!!.visibility = View.GONE
            fragment.updateAdapter()

            val finishedDialogFragment = ImportFinishedDialogFragment()

            val args = Bundle()

            args.putInt(MOVIE_COUNT, if (importExportObject.isMoviesSuccessfullyImported) importExportObject.movies.size else -1)
            args.putInt(SERIES_COUNT, if (importExportObject.isSeriesSuccessfullyImported) importExportObject.series.size else -1)

            finishedDialogFragment.arguments = args

            finishedDialogFragment.show(asyncOutputAttributes.asyncInputAttribute.fragmentManager, "")
        }
    }

    private class AsyncInputAttribute internal constructor(internal val fragmentManager: FragmentManager, internal val baseListFragment: BaseListFragment)

    private class AsyncOutputAttributes internal constructor(internal val importExportObject: ImportExportObject, internal val asyncInputAttribute: AsyncInputAttribute)

    companion object {
        private var movieDbHelper: MovieDbHelper? = null
        private var seriesDbHelper: SeriesDbHelper? = null
    }
}