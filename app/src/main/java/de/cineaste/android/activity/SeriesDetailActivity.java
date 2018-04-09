package de.cineaste.android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeriesDetailAdapter;
import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.network.SeriesCallback;
import de.cineaste.android.network.SeriesLoader;
import de.cineaste.android.util.Constants;

public class SeriesDetailActivity extends AppCompatActivity implements ItemClickListener, SeriesDetailAdapter.SeriesStateManipulationClickListener {

    private int state;
    private ImageView poster;

    private long seriesId;
    private SeriesDbHelper seriesDbHelper;
    private SeriesLoader seriesLoader;
    private Series currentSeries;
    private RecyclerView layout;
    private Runnable updateCallBack;
    private SeriesDetailAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        MenuItem toWatchList = menu.findItem(R.id.action_to_watchlist);
        MenuItem toHistory = menu.findItem(R.id.action_to_history);
        MenuItem delete = menu.findItem(R.id.action_delete);

        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            }
        }

        switch (state) {
            case R.string.searchState:
                delete.setVisible(false);
                toHistory.setVisible(true);
                toWatchList.setVisible(true);
                break;
            case R.string.historyState:
                delete.setVisible(true);
                toHistory.setVisible(false);
                toWatchList.setVisible(true);
                break;
            case R.string.watchlistState:
                delete.setVisible(true);
                toHistory.setVisible(true);
                toWatchList.setVisible(false);
                break;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_delete:
                onDeleteClicked();
                return true;
            case R.id.action_to_history:
                onAddToHistoryClicked();
                return true;
            case R.id.action_to_watchlist:
                onAddToWatchClicked();
                return true;
        }
        return true;
    }

    @Override
    public void onDeleteClicked() {
        layout.removeCallbacks(updateCallBack);
        seriesDbHelper.delete(seriesId);
        currentSeries = null;
        onBackPressed();
    }

    @Override
    public void onAddToHistoryClicked() {
        SeriesCallback seriesCallback = null;

        switch (state) {
            case R.string.searchState:

                seriesCallback = new SeriesCallback() {
                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onSuccess(Series series) {
                        showDialogIfNeeded(series);
                    }
                };

                break;
            case R.string.watchlistState:
                showDialogIfNeeded(currentSeries);
        }

        if (seriesCallback != null) {

            seriesLoader.loadCompleteSeries(currentSeries.getId(), seriesCallback);

            Toast.makeText(this, this.getResources().getString(R.string.movieAdd,
                    currentSeries.getName()), Toast.LENGTH_SHORT).show();

            onBackPressed();
        }


    }

    private void showDialogIfNeeded(final Series series) {
        if (series.isInProduction()) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setTitle(getString(R.string.seriesSeenHeadline, series.getName()));
            alertBuilder.setMessage(R.string.seriesStillInProduction);
            alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    moveBetweenLists(series);
                }
            });
            alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //do nothing
                }
            });

            alertBuilder.create().show();
        } else {
            moveBetweenLists(series);
        }
    }

    private void moveBetweenLists(Series series) {
        if (state == R.string.searchState) {
            seriesDbHelper.addToHistory(series);
        } else if (state == R.string.watchlistState) {
            seriesDbHelper.moveToHistory(series);
        }

        onBackPressed();
    }

    @Override
    public void onAddToWatchClicked() {
        SeriesCallback callback = null;

        switch (state) {
            case R.string.searchState:
                callback = new SeriesCallback() {
                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onSuccess(Series series) {
                        seriesDbHelper.addToWatchList(series);
                    }
                };


                break;
            case R.string.historyState:
                seriesDbHelper.moveToWatchList(currentSeries);
                break;
        }

        if (callback != null) {
            seriesLoader.loadCompleteSeries(currentSeries.getId(), callback);
            Toast.makeText(this, this.getResources().getString(R.string.movieAdd,
                    currentSeries.getName()), Toast.LENGTH_SHORT).show();
        }

        onBackPressed();
    }

    @Override
    public void onItemClickListener(long itemId, View[] views) {
        Intent intent = new Intent(SeriesDetailActivity.this, SeasonDetailActivity.class);
        intent.putExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SERIES_ID, currentSeries.getId());
        intent.putExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SEASON_NUMBER, itemId);

        if(state != R.string.searchState)
            startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_detail);

        seriesDbHelper = SeriesDbHelper.getInstance(this);
        seriesLoader = new SeriesLoader(this);

        Intent intent = getIntent();
        seriesId = intent.getLongExtra(BaseDao.SeriesEntry._ID, -1);
        state = intent.getIntExtra(getString(R.string.state), -1);

        initViews();

        updateCallBack = getUpdateCallBack();
        autoUpdate();

        currentSeries = seriesDbHelper.getSeriesById(seriesId);
        if (currentSeries == null) {
            loadRequestedSeries();
        } else {
            assignData(currentSeries);
        }

        initToolbar();

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeriesDetailActivity.this, PosterActivity.class);
                intent.putExtra(PosterActivity.POSTER_PATH, currentSeries.getBackdropPath());
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
        FloatingActionButton fab = findViewById(R.id.fab);
        poster = findViewById(R.id.movie_poster);
        layout = findViewById(R.id.overlay);
        layout.setLayoutManager(new LinearLayoutManager(this));
        layout.setHasFixedSize(true);

        if (state == R.string.watchlistState) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    seriesDbHelper.episodeWatched(currentSeries);
                    currentSeries = seriesDbHelper.getSeriesById(currentSeries.getId());
                    assignData(currentSeries);
                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }
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

            seriesLoader.loadCompleteSeries(seriesId, new SeriesCallback() {
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
                public void onSuccess(final Series series) {
                    if (currentSeries == null) {
                        return;
                    }
                    series.setWatched(currentSeries.isWatched());
                    seriesDbHelper.update(series);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            assignData(series);

                        }
                    });

                }
            });
        }
    }

    private void assignData(Series series) {
        String posterUri = Constants.Companion.getPOSTER_URI_ORIGINAL()
                .replace("<posterName>", series.getBackdropPath() != null ?
                        series.getBackdropPath() : "/")
                .replace("<API_KEY>", getString(R.string.movieKey));
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(poster);

        adapter = new SeriesDetailAdapter(series, this, state, this);
        layout.setAdapter(adapter);
    }

    private void loadRequestedSeries() {
        seriesLoader.loadCompleteSeries(seriesId, new SeriesCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccess(final Series series) {
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

    private void slideIn() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.to_top);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        layout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                currentSeries = seriesDbHelper.getSeriesById(seriesId);
                if (currentSeries != null) {
                    if (adapter != null) {
                        adapter.updateSeries(currentSeries);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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
