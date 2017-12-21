package de.cineaste.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.cineaste.android.R;
import de.cineaste.android.adapter.SeriesDetailAdapter;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.SeriesDbHelper;
import de.cineaste.android.entity.Series;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;
import de.cineaste.android.util.Constants;
import de.cineaste.android.util.DateAwareGson;

public class SeriesDetailActivity extends AppCompatActivity implements ItemClickListener {

    private int state;
    private long seriesId;
    private SeriesDbHelper dbHelper;
    private Series currentSeries;
    private Gson gson;
    private ImageView poster;
    private RecyclerView layout;
    private Runnable updateCallBack;

    @Override
    public void onItemClickListener(long itemId, View[] views) {
        Intent intent = new Intent(SeriesDetailActivity.this, SeasonDetailActivity.class);
        intent.putExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SERIES_ID, currentSeries.getId());
        intent.putExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SEASON_NUMBER, itemId);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_detail);
        gson = new DateAwareGson().getGson();

        dbHelper = SeriesDbHelper.getInstance(this);

        Intent intent = getIntent();
        seriesId = intent.getLongExtra(BaseDao.SeriesEntry._ID, -1);
        state = intent.getIntExtra(getString(R.string.state), -1);

        initViews();

        updateCallBack = getUpdateCallBack();
        autoUpdate();

        currentSeries = dbHelper.readSeries(seriesId);
        if (currentSeries == null) {
            loadRequestedSeries();
        } else {
            assignData(currentSeries);
        }

        initToolbar();

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeriesDetailActivity.this, MoviePosterActivity.class);
                intent.putExtra(MoviePosterActivity.POSTER_PATH, currentSeries.getBackdropPath());
                slideOut();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideIn();
    }

    private void initViews() {
        poster = findViewById(R.id.movie_poster);
        layout = findViewById(R.id.overlay);
        layout.setLayoutManager(new LinearLayoutManager(this));
        layout.setHasFixedSize(true);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        setTitleIfNeeded();
    }

    private void setTitleIfNeeded() {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(currentSeries.getName());

                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void autoUpdate() {
        layout.removeCallbacks(updateCallBack);
        layout.postDelayed(updateCallBack, 1000);
    }

    @NonNull
    private Runnable getUpdateCallBack() {
        return new Runnable() {
            @Override
            public void run() {
                updateSeries();
            }
        };
    }

    private void updateSeries() {
        if (state != R.string.searchState) {

            final NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getSeries(seriesId));
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
                    final Series series = gson.fromJson(response.getResponseReader(), Series.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            assignData(series);
                            series.setWatched(currentSeries.isWatched());
                            series.setCurrentNumberOfSeason(currentSeries.getCurrentNumberOfSeason());
                            series.setCurrentNumberOfEpisode(currentSeries.getCurrentNumberOfEpisode());
                            series.setCurrentPosterPath(currentSeries.getCurrentPosterPath());
                            dbHelper.createOrUpdate(series);
                        }
                    });
                }
            });
        }
    }

    private void assignData(Series series) {
        String posterUri = Constants.POSTER_URI_ORIGINAL
                .replace("<posterName>", series.getBackdropPath() != null ?
                        series.getBackdropPath() : "/")
                .replace("<API_KEY>", getString(R.string.movieKey));
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(poster);

        layout.setAdapter(new SeriesDetailAdapter(series, this));
    }

    private void loadRequestedSeries() {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getSeries(seriesId));
        client.sendRequest(new NetworkCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccess(NetworkResponse response) {
                final Series series = gson.fromJson(response.getResponseReader(), Series.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentSeries = series;
                        assignData(series);
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

           /* case R.id.action_delete:
                onDeleteClicked();
                return true;
            case R.id.action_to_watchedlist:
                onAddToWatchedClicked();
                return true;
            case R.id.action_to_watchlist:
                onAddToWatchClicked();
                return true;*/
        }
        return true;
    }

    private void slideIn() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.to_top);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        layout.startAnimation(animation);
    }

    private void slideOut() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.to_bottom);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        layout.startAnimation(animation);
    }

    @Override
    public void onBackPressed() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.to_bottom);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        layout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SeriesDetailActivity.super.onBackPressed();
                layout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void showNetworkError() {
        Snackbar snackbar = Snackbar
                .make(layout, R.string.noInternet, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
