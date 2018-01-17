package de.cineaste.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import de.cineaste.android.activity.AboutActivity;
import de.cineaste.android.activity.MovieNightActivity;
import de.cineaste.android.database.ExportService;
import de.cineaste.android.database.ImportService;
import de.cineaste.android.database.dbHelper.EpisodeDbHelper;
import de.cineaste.android.database.dbHelper.MovieDbHelper;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.database.dbHelper.UserDbHelper;
import de.cineaste.android.entity.ImportExportObject;
import de.cineaste.android.entity.User;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.fragment.BaseListFragment;
import de.cineaste.android.fragment.BaseMovieListFragment;
import de.cineaste.android.fragment.ImportFinishedDialogFragment;
import de.cineaste.android.fragment.SeriesListFragment;
import de.cineaste.android.fragment.UserInputFragment;
import de.cineaste.android.fragment.WatchState;

import static de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.EPISODES_COUNT;
import static de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.MOVIE_COUNT;
import static de.cineaste.android.fragment.ImportFinishedDialogFragment.BundleKeyWords.SERIES_COUNT;

public class MainActivity extends AppCompatActivity implements UserInputFragment.UserNameListener {

    private FragmentManager fm;
    private View contentContainer;
    private UserDbHelper userDbHelper;
    private static MovieDbHelper movieDbHelper;
    private static SeriesDbHelper seriesDbHelper;
    private static EpisodeDbHelper episodeDbHelper;
    private TextView userName;

    private DrawerLayout drawerLayout;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (fm.getBackStackEntryCount() > 1)
                super.onBackPressed();
            else
                finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (fm.getBackStackEntryCount() > 1)
            fm.popBackStack();
        else
            drawerLayout.openDrawer(GravityCompat.START);

        return false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String permissions[],
            @NonNull int grantResults[]) {
        switch (requestCode) {
            default:
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                            this,
                            R.string.missing_permission,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDbHelper = UserDbHelper.getInstance(this);
        movieDbHelper = MovieDbHelper.getInstance(this);
        seriesDbHelper = SeriesDbHelper.getInstance(this);
        episodeDbHelper = EpisodeDbHelper.getInstance(this);
        contentContainer = findViewById(R.id.content_container);

        fm = getSupportFragmentManager();

        initToolbar();
        initNavDrawer();

        checkPermissions();

        if (savedInstanceState == null) {
            replaceFragment(fm, getBaseWatchlistFragment(WatchState.WATCH_STATE));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        User user = userDbHelper.getUser();
        if (user != null && userName != null) {
            userName.setText(user.getUserName());
        }
    }

    private void replaceFragment(FragmentManager fm, Fragment fragment) {
        fm.beginTransaction()
                .replace(
                        R.id.content_container,
                        fragment, fragment.getClass().getName())
                .addToBackStack(null)
                .commit();
    }

    private void replaceFragmentPopBackStack(FragmentManager fm, Fragment fragment) {
        fm.popBackStack();
        replaceFragment(fm, fragment);
    }

    private void checkPermissions() {
        String storage = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(this, storage) == PackageManager.PERMISSION_GRANTED)
            return;


        ActivityCompat.requestPermissions(this, new String[]{storage}, 1);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initNavDrawer() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new CustomDrawerClickListener());

        colorMenu(navigationView.getMenu());

        userName = navigationView.getHeaderView(0).findViewById(R.id.username);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open, R.string.close
        );
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void colorMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);

            if (menuItem.getTitle() != null) {
                SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
                spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.toolbar_text)), 0, spanString.length(), 0);
                menuItem.setTitle(spanString);
            }

            Drawable drawable = menuItem.getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            }

            Menu subMenu = menuItem.getSubMenu();
            if (subMenu != null) {
                colorMenu(subMenu);
            }
        }
    }

    private class CustomDrawerClickListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.show_movie_watchlist:
                    BaseMovieListFragment watchlistFragment = getBaseWatchlistFragment(WatchState.WATCH_STATE);
                    replaceFragmentPopBackStack(fm, watchlistFragment);
                    break;
                case R.id.show_movie_watchedlist:
                    BaseMovieListFragment watchedlistFragment = getBaseWatchlistFragment(WatchState.WATCHED_STATE);
                    replaceFragmentPopBackStack(fm, watchedlistFragment);
                    break;
                case R.id.show_series_watchlist:
                    SeriesListFragment seriesWatchlistFragment = getSeriesListFragment(WatchState.WATCH_STATE);
                    replaceFragmentPopBackStack(fm, seriesWatchlistFragment);
                    break;
                case R.id.show_series_watchedlist:
                    SeriesListFragment seriesWatchedlistFragment = getSeriesListFragment(WatchState.WATCHED_STATE);
                    replaceFragmentPopBackStack(fm, seriesWatchedlistFragment);
                    break;
                case R.id.exportMovies:
                    exportMovies();
                    break;
                case R.id.importMovies:
                    importMovies();
                    break;
                case R.id.about:
                    Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                    break;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    private void exportMovies() {
        ImportExportObject importExportObject = new ImportExportObject();
        importExportObject.setMovies(movieDbHelper.readAllMovies());
        importExportObject.setSeries(seriesDbHelper.readAllSeries());
        importExportObject.setEpisodes(episodeDbHelper.readAllEpisodes());
        importExportObject = ExportService.export(importExportObject);

        int snackBarMessage = R.string.exportFailed;

        if (importExportObject.isSuccessfullyImported()) {
            snackBarMessage = R.string.exportSucceeded;
        }

        Snackbar snackbar = Snackbar
                .make(contentContainer, snackBarMessage, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void importMovies() {
        BaseListFragment baseListFragment;
        try {
            baseListFragment = (BaseListFragment) fm.findFragmentByTag(BaseMovieListFragment.class.getName());
        } catch (Exception ex) {
            baseListFragment = null;
        }

        if (baseListFragment == null) {
            try {
                baseListFragment = (BaseListFragment) fm.findFragmentByTag(SeriesListFragment.class.getName());
            } catch (Exception ex) {
                baseListFragment = null;
            }
        }

        if (baseListFragment == null) {
            return;
        }
        baseListFragment.getProgressbar().setVisibility(View.VISIBLE);
        new AsyncImporter().execute(new AsyncInputAttribute(getSupportFragmentManager(), baseListFragment));
    }

    @NonNull
    private BaseMovieListFragment getBaseWatchlistFragment(WatchState state) {
        BaseMovieListFragment watchlistFragment = new BaseMovieListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(
                WatchState.WATCH_STATE_TYPE.name(),
                state.name());
        watchlistFragment.setArguments(bundle);
        return watchlistFragment;
    }

    @NonNull
    private SeriesListFragment getSeriesListFragment(WatchState state) {
        SeriesListFragment seriesListFragment = new SeriesListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(
                WatchState.WATCH_STATE_TYPE.name(),
                state.name());
        seriesListFragment.setArguments(bundle);
        return seriesListFragment;
    }

    @Override
    public void onFinishUserDialog(String userName) {
        if (!userName.isEmpty()) {
            userDbHelper.createUser(new User(userName));
        }

        Intent intent = new Intent(MainActivity.this, MovieNightActivity.class);
        startActivity(intent);
    }


    private static class AsyncImporter extends AsyncTask<AsyncInputAttribute, Void, AsyncOutputAttributes> {

        @Override
        protected AsyncOutputAttributes doInBackground(AsyncInputAttribute... asyncInputAttributes) {
            ImportExportObject importExportObject = ImportService.importFiles();

            //todo find a better solution to save all files
            for (Movie movie : importExportObject.getMovies()) {
                movieDbHelper.createOrUpdate(movie);
            }

            for (Series series : importExportObject.getSeries()) {
                seriesDbHelper.createOrUpdate(series);
            }

            for (Episode episode : importExportObject.getEpisodes()) {
                episodeDbHelper.createOrUpdate(episode);
            }

            return new AsyncOutputAttributes(importExportObject, asyncInputAttributes[0]);
        }

        @Override
        protected void onPostExecute(AsyncOutputAttributes asyncOutputAttributes) {
            super.onPostExecute(asyncOutputAttributes);

            ImportExportObject importExportObject = asyncOutputAttributes.getImportExportObject();

            BaseListFragment fragment = asyncOutputAttributes.getAsyncInputAttribute().getBaseListFragment();
            fragment.getProgressbar().setVisibility(View.GONE);
            fragment.updateAdapter();

            ImportFinishedDialogFragment finishedDialogFragment = new ImportFinishedDialogFragment();

            Bundle args = new Bundle();

            args.putInt(MOVIE_COUNT, importExportObject.isMoviesSuccessfullyImported() ? importExportObject.getMovies().size() : -1);
            args.putInt(SERIES_COUNT, importExportObject.isSeriesSuccessfullyImported() ? importExportObject.getSeries().size() : -1);
            args.putInt(EPISODES_COUNT, importExportObject.isEpisodesSuccessfullyImported() ? importExportObject.getEpisodes().size() : -1);

            finishedDialogFragment.setArguments(args);

            finishedDialogFragment.show(asyncOutputAttributes.getAsyncInputAttribute().getFragmentManager(), "");
        }
    }

    private static class AsyncInputAttribute {
        private final FragmentManager fragmentManager;
        private final BaseListFragment baseListFragment;

        AsyncInputAttribute(FragmentManager fragmentManager, BaseListFragment baseListFragment) {
            this.fragmentManager = fragmentManager;
            this.baseListFragment = baseListFragment;
        }

        FragmentManager getFragmentManager() {
            return fragmentManager;
        }

        BaseListFragment getBaseListFragment() {
            return baseListFragment;
        }
    }

    private static class AsyncOutputAttributes {
        private final ImportExportObject importExportObject;
        private final AsyncInputAttribute asyncInputAttribute;

        AsyncOutputAttributes(ImportExportObject importExportObject, AsyncInputAttribute asyncInputAttribute) {
            this.importExportObject = importExportObject;
            this.asyncInputAttribute = asyncInputAttribute;
        }

        ImportExportObject getImportExportObject() {
            return importExportObject;
        }

        AsyncInputAttribute getAsyncInputAttribute() {
            return asyncInputAttribute;
        }
    }

}