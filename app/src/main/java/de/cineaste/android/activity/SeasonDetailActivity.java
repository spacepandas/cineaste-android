package de.cineaste.android.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.adapter.SeasonPagerAdapter;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.EpisodeDbHelper;
import de.cineaste.android.database.SeriesDbHelper;
import de.cineaste.android.entity.Season;
import de.cineaste.android.entity.Series;

public class SeasonDetailActivity extends AppCompatActivity {

    private SeasonPagerAdapter adapter;
    private ViewPager viewPager;
    private Series currentSeries;
    private SeriesDbHelper seriesDbHelper;
    private EpisodeDbHelper episodeDbHelper;

    private long seriesId;
    private long seasonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_detail);

        seriesDbHelper = SeriesDbHelper.getInstance(this);
        episodeDbHelper = EpisodeDbHelper.getInstance(this);

        Intent intent = getIntent();
        seriesId = intent.getLongExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SERIES_ID, -1);
        seasonId = intent.getLongExtra(BaseDao.SeasonEntry.COLUMN_SEASON_SEASON_NUMBER, -1);

        currentSeries = seriesDbHelper.readSeries(seriesId);

        adapter = new SeasonPagerAdapter(getSupportFragmentManager(), currentSeries, getResources());
        viewPager = findViewById(R.id.pager);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentSeasonIndex());

        initToolbar();

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

        setTitle(currentSeries.getName());
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
