package de.cineaste.android.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cineaste.android.R;
import de.cineaste.android.adapter.SeasonAdapter;
import de.cineaste.android.adapter.SeasonPagerAdapter;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.SeriesDbHelper;
import de.cineaste.android.entity.Series;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;
import de.cineaste.android.util.Constants;
import de.cineaste.android.util.DateAwareGson;

public class SeriesDetailActivity extends AppCompatActivity {

    private int state;
    private long seriesId;
    private SeriesDbHelper dbHelper;
    private SeasonPagerAdapter adapter;
    private ViewPager viewPager;
    private Series currentSeries;
    private Gson gson;
    private ImageView poster;
    private TextView rating;
    private TextView title;
    private TextView releaseDate;
    private TextView seasons;
    private TextView episodes;
    private TextView currentStatus;
    private RecyclerView seasonsRv;
    private NestedScrollView layout;
    private Runnable updateCallBack;


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


        // adapter = new SeasonPagerAdapter(getSupportFragmentManager(), currentSeries, getResources());
        //  viewPager = findViewById(R.id.pager);

        //  viewPager.setAdapter(adapter);

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
        releaseDate = findViewById(R.id.movieReleaseDate);
        poster = findViewById(R.id.movie_poster);
        rating = findViewById(R.id.rating);
        title = findViewById(R.id.movieTitle);
        layout = findViewById(R.id.overlay);
        episodes = findViewById(R.id.episodes);
        seasons = findViewById(R.id.seasons);
        currentStatus = findViewById(R.id.currentStatus);
        seasonsRv = findViewById(R.id.seasonPoster);

        int numberOfColumns = determineNumberOfColumns();

       /* StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                numberOfColumns,
                StaggeredGridLayoutManager.VERTICAL);*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        seasonsRv.setLayoutManager(layoutManager);
        seasonsRv.setItemAnimator(new DefaultItemAnimator());
        seasonsRv.setNestedScrollingEnabled(false);

        //todo remove null
        SeasonAdapter adapter = new SeasonAdapter(this, null, seriesId);
        seasonsRv.setAdapter(adapter);

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
                    title.setVisibility(View.GONE);

                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    title.setVisibility(View.VISIBLE);
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
                            dbHelper.createOrUpdate(series);
                        }
                    });
                }
            });
        }
    }

    private void assignData(Series series) {
        TextView descriptionTv = findViewById(R.id.movie_description);

        String description = series.getDescription();
        descriptionTv.setText(
                (description == null || description.isEmpty())
                        ? getString(R.string.noDescription) : description);

        title.setText(series.getName());
        if (series.getReleaseDate() != null) {
            releaseDate.setText(convertDate(series.getReleaseDate()));
            releaseDate.setVisibility(View.VISIBLE);
        } else {
            releaseDate.setVisibility(View.GONE);
        }

        rating.setText(String.valueOf(series.getVoteAverage()));
        episodes.setText(getString(R.string.episodes, String.valueOf(series.getNumberOfEpisodes())));
        seasons.setText(getString(R.string.seasons, String.valueOf(series.getNumberOfSeasons())));
        currentStatus.setText(getString(R.string.currentStatus,
                String.valueOf(series.getCurrentNumberOfSeason()),
                String.valueOf(series.getCurrentNumberOfEpisode())));


        String posterUri = Constants.POSTER_URI_ORIGINAL
                .replace("<posterName>", series.getBackdropPath() != null ?
                        series.getBackdropPath() : "/")
                .replace("<API_KEY>", getString(R.string.movieKey));
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(poster);
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

    private String convertDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", getResources().getConfiguration().locale);
        return simpleDateFormat.format(date);
    }

    private int determineNumberOfColumns() {
        int numberOfColumns = 2;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numberOfColumns = 4;
        }
        return numberOfColumns;
    }
}
