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
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesSearchQueryAdapter;
import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dbHelper.EpisodeDbHelper;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.entity.series.Season;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;
import de.cineaste.android.util.DateAwareGson;

public class SeriesSearchActivity extends AppCompatActivity implements ItemClickListener, SeriesSearchQueryAdapter.OnSeriesStateChange {

    private final Gson gson = new Gson();
    private final SeriesDbHelper db = SeriesDbHelper.getInstance(this);
    private EpisodeDbHelper episodeDbHelper;
    private SeriesSearchQueryAdapter seriesQueryAdapter;
    private RecyclerView seriesQueryRecyclerView;
    private SearchView searchView;
    private String searchText;
    private ProgressBar progressBar;

    @Override
    public void onItemClickListener(long itemId, View[] views) {
        Intent intent = new Intent(this, SeriesDetailActivity.class);
        intent.putExtra(BaseDao.SeriesEntry._ID, itemId);
        intent.putExtra(this.getString(R.string.state), R.string.searchState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(views[0], "card"),
                    Pair.create(views[1], "poster"));
            this.startActivity(intent, options.toBundle());
        } else {
            this.startActivity(intent);
        }
    }

    @Override
    public void onSeriesStateChangeListener(final Series series, int viewId, final int index) {
        NetworkCallback callback;
        switch (viewId) {
            case R.id.to_watchlist_button:
                callback = new NetworkCallback() {
                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seriesAddError(series, index);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(NetworkResponse response) {
                        Series series = gson.fromJson(response.getResponseReader(), Series.class);
                        db.createOrUpdate(series);
                        loadSeasons(series);
                    }
                };
                break;
            case R.id.history_button:
                callback = new NetworkCallback() {
                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                seriesAddError(series, index);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(NetworkResponse response) {
                        Series series = gson.fromJson(response.getResponseReader(), Series.class);
                        series.setWatched(true);
                        db.createOrUpdate(series);
                        loadSeasons(series);
                    }
                };
                break;
                default:
                    callback = null;
                    break;
        }
        if (callback != null) {
            seriesQueryAdapter.removeMovie(index);
            NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getSeries(series.getId()));
            client.sendRequest(callback);
        }

    }

    private void loadSeasons(Series series) {
        for (final Season season : series.getSeasons()) {
            if (season.getSeasonNumber() == 0) {
                continue;
            }

            NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getSeason(series.getId(), season.getSeasonNumber()));
            client.sendRequest(new NetworkCallback() {
                @Override
                public void onFailure() {
                    android.util.Log.d("mgr", "faild to load season { "+ season.getSeasonNumber() +" }");
                }

                @Override
                public void onSuccess(NetworkResponse response) {

                    Gson gson = new DateAwareGson().getGson();
                    JsonParser parser = new JsonParser();
                    JsonObject responseObject =
                            parser.parse(response.getResponseReader()).getAsJsonObject();
                    String episodesListJson = responseObject.get("episodes").toString();
                    Type listType = new TypeToken<List<Episode>>(){}.getType();
                    final List<Episode> episodes = gson.fromJson(episodesListJson, listType);
                    for (Episode episode : episodes) {
                        episode.setSeasonId(season.getId());
                        episodeDbHelper.createOrUpdate(episode);
                    }
                }
            });
        }

    }

    private void seriesAddError(Series series, int index) {
        Snackbar snackbar = Snackbar
                .make(seriesQueryRecyclerView, R.string.could_not_add_movie, Snackbar.LENGTH_LONG);
        snackbar.show();
        seriesQueryAdapter.addSerie(series, index);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_search);

        episodeDbHelper = EpisodeDbHelper.getInstance(this);

        initToolbar();

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("query", null).replace("+", " ");
        }

        progressBar = findViewById(R.id.progressBar);
        seriesQueryRecyclerView = findViewById(R.id.search_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        seriesQueryAdapter = new SeriesSearchQueryAdapter(this, this);
        seriesQueryRecyclerView.setItemAnimator(new DefaultItemAnimator());

        seriesQueryRecyclerView.setLayoutManager(layoutManager);
        seriesQueryRecyclerView.setAdapter(seriesQueryAdapter);

        getPopular();
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
    protected void onPause() {
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
                        getPopular();
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

    private void getPopular() {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getPopularSeries());
        client.sendRequest(getNetworkCallback());
    }

    private void searchRequest(String searchQuery) {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).searchSeries(searchQuery));
        client.sendRequest(getNetworkCallback());
    }

    @NonNull
    private NetworkCallback getNetworkCallback() {
        return new NetworkCallback() {
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
                Gson gson = new DateAwareGson().getGson();
                JsonParser parser = new JsonParser();
                JsonObject responseObject =
                        parser.parse(response.getResponseReader()).getAsJsonObject();
                String seriesListJson = responseObject.get("results").toString();
                Type listType = new TypeToken<List<Series>>() {
                }.getType();
                final List<Series> series = gson.fromJson(seriesListJson, listType);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seriesQueryAdapter.addSeries(series);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        };
    }

    private void showNetworkError() {
        Snackbar snackbar = Snackbar
                .make(seriesQueryRecyclerView, R.string.noInternet, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }
            imm.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
