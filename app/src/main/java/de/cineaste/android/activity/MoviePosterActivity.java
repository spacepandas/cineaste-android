package de.cineaste.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import de.cineaste.android.Constants;
import de.cineaste.android.R;
import de.markusfisch.android.scalingimageview.widget.ScalingImageView;

public class MoviePosterActivity extends AppCompatActivity {

    public static String POSTER_PATH = "posterPath";
    public static String MOVIE_NAME = "movieName";

    private ScalingImageView moviePoster;
    private String posterPath;
    private String movieName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_poster);

        moviePoster = findViewById(R.id.movie_poster);

        Intent intent = getIntent();
        posterPath = intent.getStringExtra(POSTER_PATH);
        movieName = intent.getStringExtra(MOVIE_NAME);

        initToolbar();

        loadImage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(movieName);

    }

    private void loadImage() {
        String posterUri = Constants.POSTER_URI_SMALL
                .replace("<posterName>", posterPath != null ?
                        posterPath : "/")
                .replace("<API_KEY>", getString(R.string.movieKey));
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(moviePoster);
    }
}
