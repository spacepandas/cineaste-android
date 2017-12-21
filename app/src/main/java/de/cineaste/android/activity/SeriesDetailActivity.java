package de.cineaste.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import de.cineaste.android.R;
import de.cineaste.android.adapter.SeasonPagerAdapter;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.SeriesDbHelper;
import de.cineaste.android.entity.Series;
import de.cineaste.android.util.Constants;

public class SeriesDetailActivity extends AppCompatActivity {

    private int state;
    private long seriesId;
    private SeriesDbHelper dbHelper;
    private SeasonPagerAdapter adapter;
    private ViewPager viewPager;
    private Series series;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_detail);

        dbHelper = SeriesDbHelper.getInstance(this);

        Intent intent = getIntent();
        seriesId = intent.getLongExtra(BaseDao.SeriesEntry._ID, -1);
        state = intent.getIntExtra(getString(R.string.state), -1);

        series = dbHelper.readSeries(seriesId);

        imageView = findViewById(R.id.expandedImage);
        String posterUri = Constants.POSTER_URI_ORIGINAL
                .replace("<posterName>", series.getBackdropPath() != null ?
                        series.getBackdropPath() : "/")
                .replace("<API_KEY>", getString(R.string.movieKey));
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(imageView);


        adapter = new SeasonPagerAdapter(getSupportFragmentManager(), series, getResources());
        viewPager = findViewById(R.id.pager);

        viewPager.setAdapter(adapter);

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(series.getName());
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
