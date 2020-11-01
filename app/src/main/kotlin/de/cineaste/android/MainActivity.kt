package de.cineaste.android

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import de.cineaste.android.activity.AboutActivity
import de.cineaste.android.database.ExportService
import de.cineaste.android.database.ImportService
import de.cineaste.android.database.dbHelper.MovieDbHelper
import de.cineaste.android.database.dbHelper.SeriesDbHelper
import de.cineaste.android.database.dbHelper.UserDbHelper
import de.cineaste.android.entity.ImportExportObject
import de.cineaste.android.fragment.ImportFinishedDialogFragment
import de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.Companion.MOVIE_COUNT
import de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.Companion.SERIES_COUNT
import de.cineaste.android.fragment.BaseListFragment
import de.cineaste.android.fragment.SeriesListFragment
import de.cineaste.android.fragment.MovieListFragment
import de.cineaste.android.fragment.WatchState
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var fm: FragmentManager
    private lateinit var contentContainer: View
    private lateinit var userDbHelper: UserDbHelper
    private lateinit var userName: TextView

    private lateinit var drawerLayout: DrawerLayout

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (fm.backStackEntryCount > 1)
                super.onBackPressed()
            else
                finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (fm.backStackEntryCount > 1)
            fm.popBackStack()
        else
            drawerLayout.openDrawer(GravityCompat.START)

        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty() &&
                grantResults[0] != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    R.string.missing_permission,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userDbHelper = UserDbHelper.getInstance(this)
        movieDbHelper = MovieDbHelper.getInstance(this)
        seriesDbHelper = SeriesDbHelper.getInstance(this)
        contentContainer = findViewById(R.id.content_container)

        fm = supportFragmentManager

        initToolbar()
        initNavDrawer()

        checkPermissions()

        if (savedInstanceState == null) {
            replaceFragment(
                fm,
                getBaseWatchlistFragment(WatchState.WATCH_STATE)
            )
        }
    }

    override fun onResume() {
        super.onResume()

        val user = userDbHelper.user
        if (user != null) {
            userName.text = user.userName
        }
    }

    private fun replaceFragment(fm: FragmentManager, fragment: Fragment) {
        fm.beginTransaction()
            .replace(
                R.id.content_container,
                fragment, fragment.javaClass.name
            )
            .addToBackStack(null)
            .commit()
    }

    private fun replaceFragmentPopBackStack(
        fm: FragmentManager,
        fragment: Fragment
    ) {
        fm.popBackStack()
        replaceFragment(fm, fragment)
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        val listPermissionsNeeded = ArrayList<String>()
        for (p in permissions) {
            val result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }

        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                1
            )
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
        navigationView.setNavigationItemSelectedListener(
            CustomDrawerClickListener()
        )

        colorMenu(navigationView.menu)

        userName = navigationView.getHeaderView(0).findViewById(R.id.username)

        drawerLayout = findViewById(R.id.drawer_layout)
        val drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open, R.string.close
        )
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    private fun colorMenu(menu: Menu) {
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)

            if (menuItem.title != null) {
                val spanString = SpannableString(menuItem.title.toString())
                spanString.setSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            this,
                            R.color.toolbar_text
                        )
                    ),
                    0,
                    spanString.length,
                    0
                )
                menuItem.title = spanString
            }

            val drawable = menuItem.icon
            if (drawable != null) {
                drawable.mutate()
                drawable.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                    ), PorterDuff.Mode.SRC_ATOP
                )
            }

            val subMenu = menuItem.subMenu
            if (subMenu != null) {
                colorMenu(subMenu)
            }
        }
    }

    private inner class CustomDrawerClickListener :
        NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.show_movie_watchlist -> {
                    val watchlistFragment =
                        getBaseWatchlistFragment(WatchState.WATCH_STATE)
                    replaceFragmentPopBackStack(fm, watchlistFragment)
                }
                R.id.show_movie_watchedlist -> {
                    val historyFragment =
                        getBaseWatchlistFragment(WatchState.WATCHED_STATE)
                    replaceFragmentPopBackStack(fm, historyFragment)
                }
                R.id.show_series_watchlist -> {
                    val seriesWatchlistFragment =
                        getSeriesListFragment(WatchState.WATCH_STATE)
                    replaceFragmentPopBackStack(fm, seriesWatchlistFragment)
                }
                R.id.show_series_watchedlist -> {
                    val seriesHistoryFragment =
                        getSeriesListFragment(WatchState.WATCHED_STATE)
                    replaceFragmentPopBackStack(fm, seriesHistoryFragment)
                }
                R.id.exportMovies -> createExportFile()
                R.id.importMovies -> selectImportFile()
                R.id.about -> {
                    val intent =
                        Intent(this@MainActivity, AboutActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
    }

    private fun createExportFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = "application/json"
        intent.putExtra(Intent.EXTRA_TITLE, getExportFileName())
        startActivityForResult(intent, WRITE_REQUEST_CODE)
    }

    private fun getExportFileName(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.ENGLISH)
        return "cineaste-${dateFormat.format(Date())}.json"
    }

    private fun exportMovies(uri: Uri) {
        val importExportObject = ImportExportObject()
        importExportObject.movies = movieDbHelper.readAllMovies()
        importExportObject.series = seriesDbHelper.allSeries

        val successfullyExported =
            ExportService.export(importExportObject, uri, this@MainActivity)

        var snackBarMessage = R.string.exportFailed

        if (successfullyExported) {
            snackBarMessage = R.string.exportSucceeded
        }

        val snackBar = Snackbar
            .make(contentContainer, snackBarMessage, Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    private fun selectImportFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    private fun importMovies(uri: Uri) {
        var baseListFragment: BaseListFragment?
        baseListFragment = try {
            fm.findFragmentByTag(MovieListFragment::class.java.name) as BaseListFragment
        } catch (ex: Exception) {
            null
        }

        if (baseListFragment == null) {
            baseListFragment = try {
                fm.findFragmentByTag(SeriesListFragment::class.java.name) as BaseListFragment
            } catch (ex: Exception) {
                null
            }
        }

        if (baseListFragment == null) return

        baseListFragment.progressbar.visibility = View.VISIBLE

        GlobalScope.launch {
            val importExportObject =
                ImportService.importFiles(uri, this@MainActivity)
            // todo find a better solution to save all files
            for (movie in importExportObject.movies) {
                movieDbHelper.createOrUpdate(movie)
            }

            for (series in importExportObject.series) {
                seriesDbHelper.importSeries(series)
            }

            launch(Main) {
                baseListFragment.progressbar.visibility = View.GONE
                baseListFragment.updateAdapter()

                val finishedDialogFragment = ImportFinishedDialogFragment()

                val args = Bundle()

                args.putInt(MOVIE_COUNT, importExportObject.movies.size)
                args.putInt(SERIES_COUNT, importExportObject.series.size)

                finishedDialogFragment.arguments = args

                finishedDialogFragment.show(supportFragmentManager, "")
            }
        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                READ_REQUEST_CODE -> {
                    resultData?.data?.let {
                        importMovies(it)
                    }
                }

                WRITE_REQUEST_CODE -> {
                    resultData?.data?.let {
                        exportMovies(it)
                    }
                }
            }
        }
    }

    private fun getBaseWatchlistFragment(state: WatchState): MovieListFragment {
        val watchlistFragment = MovieListFragment()
        val bundle = Bundle()
        bundle.putString(
            WatchState.WATCH_STATE_TYPE.name,
            state.name
        )
        watchlistFragment.arguments = bundle
        return watchlistFragment
    }

    private fun getSeriesListFragment(state: WatchState): SeriesListFragment {
        val seriesListFragment = SeriesListFragment()
        val bundle = Bundle()
        bundle.putString(
            WatchState.WATCH_STATE_TYPE.name,
            state.name
        )
        seriesListFragment.arguments = bundle
        return seriesListFragment
    }

    companion object {
        private const val READ_REQUEST_CODE = 42
        private const val WRITE_REQUEST_CODE = 43

        private lateinit var movieDbHelper: MovieDbHelper
        private lateinit var seriesDbHelper: SeriesDbHelper
    }
}