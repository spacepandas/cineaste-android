package de.cineaste.android.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.DateAwareGson;
import de.cineaste.android.listener.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.adapter.SearchQueryAdapter;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;

public class SearchActivity extends AppCompatActivity implements MovieClickListener, SearchQueryAdapter.OnMovieStateChange {

    private final Gson gson = new Gson();
    private final MovieDbHelper db = MovieDbHelper.getInstance(this);
    private SearchQueryAdapter movieQueryAdapter;
    private RecyclerView movieQueryRecyclerView;
    private SearchView searchView;
    private String searchText;
    private ProgressBar progressBar;

    @Override
    public void onMovieClickListener(long movieId, View[] views) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(BaseDao.MovieEntry._ID, movieId);
        intent.putExtra(this.getString(R.string.state), R.string.searchState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(views[0], "card"),
                    Pair.create(views[1], "poster"));
            this.startActivity(intent, options.toBundle());
        } else {
            this.startActivity(intent);
            // this.overridePendingTransition( R.anim.fade_out, R.anim.fade_in );
        }
    }

    @Override
    public void onMovieStateChangeListener(final Movie movie, int viewId, final int index) {
        NetworkCallback callback;
        switch (viewId) {
            case R.id.to_watchlist_button:
                callback = new NetworkCallback() {
                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                movieAddError(movie, index);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(NetworkResponse response) {
                        db.createNewMovieEntry(gson.fromJson(response.getResponseReader(), Movie.class));

                    }
                };
                break;
            case R.id.watched_button:
                callback = new NetworkCallback() {
                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                movieAddError(movie, index);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(NetworkResponse response) {
                        Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
                        movie.setWatched(true);
                        db.createNewMovieEntry(movie);
                    }
                };
                break;
            default:
                callback = null;
                break;
        }
        if (callback != null) {
            movieQueryAdapter.removeMovie(index);
            NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).get(movie.getId()));
            client.sendRequest(callback);
        }

    }

    private void movieAddError(Movie movie, int index) {
        Snackbar snackbar = Snackbar
                .make(movieQueryRecyclerView, R.string.could_not_add_movie, Snackbar.LENGTH_LONG);
        snackbar.show();
        movieQueryAdapter.addMovie(movie, index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initToolbar();

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("query", null).replace("+", " ");
        }

        progressBar = findViewById(R.id.progressBar);
        movieQueryRecyclerView = findViewById(R.id.search_recycler_view);
        RecyclerView.LayoutManager movieQueryLayoutMgr = new LinearLayoutManager(this);
        movieQueryAdapter = new SearchQueryAdapter(this, this);
        movieQueryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        movieQueryRecyclerView.setLayoutManager(movieQueryLayoutMgr);
        movieQueryRecyclerView.setAdapter(movieQueryAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
            if (searchView != null) {
                if (!TextUtils.isEmpty(searchText)) {
                    outState.putString("query", searchText);
                }
            }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle outState = new Bundle();
        outState.putString("query", searchText);
        onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    if (!query.isEmpty()) {
                        query = query.replace(" ", "+");
                        progressBar.setVisibility(View.VISIBLE);

                        scheduleSearchRequest(query);

                        searchText = query;
                    } else {
                        movieQueryAdapter.addMovies(new ArrayList<Movie>());
                    }
                    return false;
                }

            });
            if (!TextUtils.isEmpty(searchText))
                searchView.setQuery(searchText, false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    private void scheduleSearchRequest(String query) {
        searchView.removeCallbacks(getSearchRunnable(query));
        searchView.postDelayed(getSearchRunnable(query), 500);
    }

    @NonNull
    private Runnable getSearchRunnable(final String searchQuery) {
        return new Runnable() {
            @Override
            public void run() {
                searchRequest(searchQuery);
            }
        };
    }

    private void searchRequest( String searchQuery) {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).search(searchQuery));
        client.sendRequest(new NetworkCallback() {
            @Override
            public void onFailure() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNetworkError();
                    }
                });
            }

            @Override
            public void onSuccess(NetworkResponse response) {
                Gson gson = new DateAwareGson(getResources().getConfiguration().locale).getGson();
                JsonParser parser = new JsonParser();
                JsonObject responseObject =
                        parser.parse(response.getResponseReader()).getAsJsonObject();
                String movieListJson = responseObject.get("results").toString();
                Type listType = new TypeToken<List<Movie>>() {
                }.getType();
                final List<Movie> movies = gson.fromJson(movieListJson, listType);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        movieQueryAdapter.addMovies(movies);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void showNetworkError() {
        Snackbar snackbar = Snackbar
                .make(movieQueryRecyclerView, R.string.noInternet, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
