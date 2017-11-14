package de.cineaste.android;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import de.cineaste.android.activity.AboutActivity;
import de.cineaste.android.activity.MovieNightActivity;
import de.cineaste.android.database.ExportService;
import de.cineaste.android.database.ImportService;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.database.UserDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.entity.User;
import de.cineaste.android.fragment.BaseMovieListFragment;
import de.cineaste.android.fragment.UserInputFragment;
import de.cineaste.android.fragment.WatchState;

public class MainActivity extends AppCompatActivity implements UserInputFragment.UserNameListener {

    private FragmentManager fm;
    private View contentContainer;
    private UserDbHelper userDbHelper;
    private static MovieDbHelper movieDbHelper;

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
        contentContainer = findViewById(R.id.content_container);

        fm = getSupportFragmentManager();

        initToolbar();

        checkPermissions();

        if (savedInstanceState == null) {
            replaceFragment(fm, getBaseWatchlistFragment(WatchState.WATCH_STATE));
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

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new CustomDrawerClickListener());

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open, R.string.close
        );
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private class CustomDrawerClickListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.show_watchlist:
                    BaseMovieListFragment watchlistFragment = getBaseWatchlistFragment(WatchState.WATCH_STATE);
                    replaceFragmentPopBackStack(fm, watchlistFragment);
                    break;
                case R.id.show_watchedlist:
                    BaseMovieListFragment watchedlistFragment = getBaseWatchlistFragment(WatchState.WATCHED_STATE);
                    replaceFragmentPopBackStack(fm, watchedlistFragment);
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
        List<Movie> movies = movieDbHelper.readAllMovies();
        ExportService.exportMovies(movies);
        Snackbar snackbar = Snackbar
                .make(contentContainer, R.string.successfulExport, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void importMovies() {
        BaseMovieListFragment baseMovieListFragment = (BaseMovieListFragment) fm.findFragmentByTag(BaseMovieListFragment.class.getName());
        new AsyncMovieImporter().execute(baseMovieListFragment);
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

    @Override
    public void onFinishUserDialog(String userName) {
        if (!userName.isEmpty()) {
            userDbHelper.createUser(new User(userName));
        }

        Intent intent = new Intent(MainActivity.this, MovieNightActivity.class);
        startActivity(intent);
    }

    private static class AsyncMovieImporter extends AsyncTask<BaseMovieListFragment, Void, AsyncAttributes> {

        @Override
        protected AsyncAttributes doInBackground(BaseMovieListFragment... fragments) {
            List<Movie> movies = ImportService.importMovies();
            int snackBarMessage;
            if (movies.size() == 0) {
                snackBarMessage = R.string.unsuccessfulImport;
            } else {
                for (Movie current : movies) {
                    movieDbHelper.createOrUpdate(current);
                }
                snackBarMessage = R.string.successfulImport;
            }
            BaseMovieListFragment fragment = fragments[0];
            Snackbar snackbar = Snackbar.make(fragment.getRecyclerView(), snackBarMessage, Snackbar.LENGTH_SHORT);

            return new AsyncAttributes(snackbar, fragment);
        }

        @Override
        protected void onPostExecute(AsyncAttributes asyncAttributes) {
            super.onPostExecute(asyncAttributes);
            asyncAttributes.getSnackbar().show();
            asyncAttributes.getListFragment().updateAdapter();
        }
    }

    private static class AsyncAttributes {
        private final Snackbar snackbar;
        private final BaseMovieListFragment listFragment;

        private AsyncAttributes(Snackbar snackbar, BaseMovieListFragment listFragment) {
            this.snackbar = snackbar;
            this.listFragment = listFragment;
        }

        private Snackbar getSnackbar() {
            return snackbar;
        }

        private BaseMovieListFragment getListFragment() {
            return listFragment;
        }
    }
}