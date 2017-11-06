package de.cineaste.android.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import de.cineaste.android.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String GITHUB_URL = "https://github.com/marcelgross90/Cineaste";
    private static final String MOVIE_DB_URL = "https://www.themoviedb.org/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolbar();

        ImageView githubLogo = findViewById(R.id.github_logo);
        ImageView movieDbLogo = findViewById(R.id.themoviedb_logo);

        githubLogo.setOnClickListener(this);
        movieDbLogo.setOnClickListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.about);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void openWebsite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.github_logo:
                openWebsite(GITHUB_URL);
                break;
            case R.id.themoviedb_logo:
                openWebsite(MOVIE_DB_URL);
                break;
        }
    }
}
