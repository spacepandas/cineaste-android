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

import java.lang.reflect.Type;

import de.cineaste.android.R;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkResponse;
import de.cineaste.android.util.DateAwareGson;


public abstract class AbstractSearchActivity extends AppCompatActivity implements ItemClickListener {

    final Gson gson = new DateAwareGson().getGson();
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private SearchView searchView;
    private String searchText;

    protected abstract Intent getIntentForDetailActivity(long itemId);
    protected abstract int getLayout();
    protected abstract void initAdapter();
    protected abstract RecyclerView.Adapter getListAdapter();
    protected abstract void getSuggestions();
    protected abstract void searchRequest( String searchQuery);
    protected abstract Type getListType();
    protected abstract Runnable getRunnable(final String json, final Type listType);


    @Override
    public void onItemClickListener(long itemId, View[] views) {
        Intent intent = getIntentForDetailActivity(itemId);

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());

        initToolbar();

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("query", null).replace("+", " ");
        }

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.search_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        initAdapter();
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(getListAdapter());

        getSuggestions();
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
                        getSuggestions();
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

    private void showNetworkError() {
        Snackbar snackbar = Snackbar
                .make(recyclerView, R.string.noInternet, Snackbar.LENGTH_LONG);
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

    @NonNull
    NetworkCallback getNetworkCallback() {
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
                JsonParser parser = new JsonParser();
                JsonObject responseObject =
                        parser.parse(response.getResponseReader()).getAsJsonObject();
                final String json = responseObject.get("results").toString();
                final Type listType = getListType();

                runOnUiThread(getRunnable(json, listType));
            }
        };
    }
}
