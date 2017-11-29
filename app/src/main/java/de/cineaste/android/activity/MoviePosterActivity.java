package de.cineaste.android.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;

import de.cineaste.android.Constants;
import de.cineaste.android.R;
import de.markusfisch.android.scalingimageview.widget.ScalingImageView;

public class MoviePosterActivity extends AppCompatActivity {

    public static String POSTER_PATH = "posterPath";

    private ScalingImageView moviePoster;
    private String posterPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        posterPath = intent.getStringExtra(POSTER_PATH);

        moviePoster = new ScalingImageView(this);
        setTransitionNameIfNecessary();
        moviePoster.setImageDrawable(getResources().getDrawable(R.drawable.placeholder_poster));
        setContentView(moviePoster);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        loadImage();
    }

    @TargetApi(21)
    private void setTransitionNameIfNecessary(){
        moviePoster.setTransitionName("poster");
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
