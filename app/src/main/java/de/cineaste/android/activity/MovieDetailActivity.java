package de.cineaste.android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cineaste.android.util.Constants;
import de.cineaste.android.util.DateAwareGson;
import de.cineaste.android.R;
import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dbHelper.MovieDbHelper;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;

public class MovieDetailActivity extends AppCompatActivity {

    private Gson gson;
    private int state;
    private ImageView poster;

    private MovieDbHelper movieDbHelper;
    private long movieId;
    private Movie currentMovie;
    private TextView rating;
    private TextView movieTitle;
    private TextView movieReleaseDate;
    private TextView movieRuntime;
    private NestedScrollView layout;
    private Runnable updateCallBack;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        MenuItem toWatchList = menu.findItem(R.id.action_to_watchlist);
        MenuItem toHistory = menu.findItem(R.id.action_to_history);
        MenuItem delete = menu.findItem(R.id.action_delete);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
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

    private void onDeleteClicked() {
        movieDbHelper.deleteMovieFromWatchlist(currentMovie);
        layout.removeCallbacks(updateCallBack);
        onBackPressed();
    }

    private void onAddToHistoryClicked() {
        NetworkCallback callback = null;

        switch (state) {
            case R.string.searchState:
                callback = new NetworkCallback() {
                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onSuccess(NetworkResponse response) {
                        Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
                        movie.setWatched(true);
                        movieDbHelper.createOrUpdate(movie);
                    }
                };
                break;
            case R.string.watchlistState:
                currentMovie.setWatched(true);
                movieDbHelper.createOrUpdate(currentMovie);
                break;
        }


        if (callback != null) {
            NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getMovie(currentMovie.getId()));
            client.sendRequest(callback);
            Toast.makeText(this, this.getResources().getString(R.string.movieAdd,
                    currentMovie.getTitle()), Toast.LENGTH_SHORT).show();
        }

        onBackPressed();

    }

    private void onAddToWatchClicked() {
        NetworkCallback callback = null;

        switch (state) {
            case R.string.searchState:
                callback = new NetworkCallback() {
                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onSuccess(NetworkResponse response) {
                        movieDbHelper.createOrUpdate(gson.fromJson(response.getResponseReader(), Movie.class));
                    }
                };
                break;
            case R.string.historyState:
                currentMovie.setWatched(false);
                movieDbHelper.createOrUpdate(currentMovie);
                break;
        }

        if (callback != null) {
            NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getMovie(currentMovie.getId()));
            client.sendRequest(callback);
            Toast.makeText(this, this.getResources().getString(R.string.movieAdd,
                    currentMovie.getTitle()), Toast.LENGTH_SHORT).show();
        }

        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        gson = new DateAwareGson().getGson();

        Intent intent = getIntent();
        movieId = intent.getLongExtra(BaseDao.MovieEntry._ID, -1);
        state = intent.getIntExtra(getString(R.string.state), -1);

        initViews();

        movieDbHelper = MovieDbHelper.getInstance(this);

        updateCallBack = getUpdateCallback();
        autoUpdateMovie();

        currentMovie = movieDbHelper.readMovie(movieId);
        if (currentMovie == null) {
            loadRequestedMovie();
        } else {
            assignData(currentMovie);
        }

        initToolbar();

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailActivity.this, PosterActivity.class);
                intent.putExtra(PosterActivity.POSTER_PATH, currentMovie.getPosterPath());
                slideOut();
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        movieReleaseDate = findViewById(R.id.movieReleaseDate);
        poster = findViewById(R.id.movie_poster);
        rating = findViewById(R.id.rating);
        movieTitle = findViewById(R.id.movieTitle);
        movieRuntime = findViewById(R.id.movieRuntime);
        layout = findViewById(R.id.overlay);

        Button deleteBtn = findViewById(R.id.delete_button);
        Button historyBtn = findViewById(R.id.history_button);
        Button watchListBtn = findViewById(R.id.to_watchlist_button);

        switch (state) {
            case R.string.searchState:
                deleteBtn.setVisibility(View.GONE);
                historyBtn.setVisibility(View.VISIBLE);
                watchListBtn.setVisibility(View.VISIBLE);
                break;
            case R.string.historyState:
                deleteBtn.setVisibility(View.VISIBLE);
                historyBtn.setVisibility(View.GONE);
                watchListBtn.setVisibility(View.VISIBLE);
                break;
            case R.string.watchlistState:
                deleteBtn.setVisibility(View.VISIBLE);
                historyBtn.setVisibility(View.VISIBLE);
                watchListBtn.setVisibility(View.GONE);
                break;
        }

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteClicked();
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddToHistoryClicked();
            }
        });

        watchListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddToWatchClicked();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        slideIn();
    }

    private void loadRequestedMovie() {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getMovie(movieId));
        client.sendRequest(new NetworkCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccess(NetworkResponse response) {
                final Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentMovie = movie;
                        assignData(movie);
                    }
                });
            }
        });
    }

    private void assignData(Movie currentMovie) {
        TextView movieDescription = findViewById(R.id.movie_description);

        String description = currentMovie.getDescription();
        movieDescription.setText(
                (description == null || description.isEmpty())
                        ? getString(R.string.noDescription) : description);

        movieTitle.setText(currentMovie.getTitle());
        if (currentMovie.getReleaseDate() != null) {
            movieReleaseDate.setText(convertDate(currentMovie.getReleaseDate()));
            movieReleaseDate.setVisibility(View.VISIBLE);
        } else {
            movieReleaseDate.setVisibility(View.GONE);
        }
        movieRuntime.setText(getString(R.string.runtime, currentMovie.getRuntime()));
        rating.setText(String.valueOf(currentMovie.getVoteAverage()));


        String posterUri = Constants.Companion.getPOSTER_URI_SMALL()
                .replace("<posterName>", currentMovie.getPosterPath() != null ?
                        currentMovie.getPosterPath() : "/")
                .replace("<API_KEY>", getString(R.string.movieKey));
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(poster);
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
                    collapsingToolbarLayout.setTitle(currentMovie.getTitle());
                    movieTitle.setVisibility(View.GONE);

                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    movieTitle.setVisibility(View.VISIBLE);
                    isShow = false;
                }
            }
        });
    }

    private void autoUpdateMovie() {
        layout.removeCallbacks(updateCallBack);
        layout.postDelayed(updateCallBack, 1000);
    }

    @NonNull
    private Runnable getUpdateCallback() {
        return new Runnable() {
            @Override
            public void run() {
                updateMovie();
            }
        };
    }

    private void updateMovie() {
        if (state != R.string.searchState) {
            final NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getMovie(movieId));
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
                    final Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            assignData(movie);
                            updateMovieDetails(movie);
                            movieDbHelper.createOrUpdate(currentMovie);
                        }
                    });
                }
            });
        }
    }

    private void updateMovieDetails(Movie movie) {
        currentMovie.setTitle(movie.getTitle());
        currentMovie.setRuntime(movie.getRuntime());
        currentMovie.setVoteAverage(movie.getVoteAverage());
        currentMovie.setVoteCount(movie.getVoteCount());
        currentMovie.setDescription(movie.getDescription());
        currentMovie.setPosterPath(movie.getPosterPath());
        currentMovie.setReleaseDate(movie.getReleaseDate());
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
                MovieDetailActivity.super.onBackPressed();
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
}
