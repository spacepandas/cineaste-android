package de.cineaste.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.adapter.series.SeasonPagerAdapter;
import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dbHelper.SeriesDbHelper;
import de.cineaste.android.entity.series.Season;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;
import de.cineaste.android.util.Constants;
import de.cineaste.android.util.DateAwareGson;

public class SeasonDetailActivity extends AppCompatActivity {

    private Series currentSeries;
    private ImageView poster;

    private long seriesId;
    private long seasonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_detail);

        SeriesDbHelper seriesDbHelper = SeriesDbHelper.getInstance(this);

        poster = findViewById(R.id.poster_image_view);

        Intent intent = getIntent();
        seriesId = intent.getLongExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SERIES_ID, -1);
        seasonId = intent.getLongExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SEASON_NUMBER, -1);

        currentSeries = seriesDbHelper.readSeries(seriesId);
        if (currentSeries == null) {
            loadSeries();
        } else {
            assignData(currentSeries);
        }

        initToolbar();

    }

    private void assignData(Series series) {
        SeasonPagerAdapter adapter = new SeasonPagerAdapter(getSupportFragmentManager(), series, getResources());
        ViewPager viewPager = findViewById(R.id.pager);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentSeasonIndex());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //do nothing
            }

            @Override
            public void onPageSelected(int position) {
                setPoster(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //do nothing
            }
        });

        setPoster(currentSeasonIndex());
    }

    private void loadSeries() {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getSeries(seriesId));
        client.sendRequest(new NetworkCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccess(NetworkResponse response) {
                Gson gson = new DateAwareGson().getGson();
                final Series series = gson.fromJson(response.getResponseReader(), Series.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Season> seasons = series.getSeasons();
                        for (Season season : seasons) {
                            //remove season which contains only special episodes
                            if (season.getSeasonNumber() == 0) {
                                seasons.remove(season);
                                break;
                            }
                        }
                        currentSeries = series;
                        assignData(series);
                    }
                });
            }
        });
    }

    public void setPoster(int position) {
        Season season = currentSeries.getSeasons().get(position);
        final String posterPath = season.getPosterPath();

        String posterUri = Constants.POSTER_URI_SMALL
                .replace("<posterName>", posterPath)
                .replace("<API_KEY>", getString(R.string.movieKey));
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(poster);

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeasonDetailActivity.this, PosterActivity.class);
                intent.putExtra(PosterActivity.POSTER_PATH, posterPath);
                startActivity(intent);
            }
        });
    }

    private int currentSeasonIndex() {
        List<Season> seasons = currentSeries.getSeasons();
        for (int i = 0; i < seasons.size(); i++) {
            if (seasons.get(i).getId() == seasonId) {
                return i;
            }
        }

        return 0;
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
}
