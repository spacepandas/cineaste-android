package de.cineaste.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;

public class MovieDetailActivity extends AppCompatActivity {

    private final Gson gson = new Gson();
    private int state;
    private ImageView moviePoster;
    private MovieDbHelper movieDbHelper;
    private long movieId;
    private Movie currentMovie;
    private TextView rating;
    private TextView movieTitle;
    private TextView movieRuntime;
    private NestedScrollView layout;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        MenuItem toWatchList = menu.findItem(R.id.action_to_watchlist);
        MenuItem toWatchedList = menu.findItem(R.id.action_to_watchedlist);
        MenuItem delete = menu.findItem(R.id.action_delete);

        switch (state) {
            case R.string.searchState:
                delete.setVisible(false);
                toWatchedList.setVisible(true);
                toWatchList.setVisible(true);
                break;
            case R.string.watchedlistState:
                delete.setVisible(true);
                toWatchedList.setVisible(false);
                toWatchList.setVisible(false);
                break;
            case R.string.watchlistState:
                delete.setVisible(true);
                toWatchedList.setVisible(true);
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
                movieDbHelper.deleteMovieFromWatchlist(currentMovie);
                return true;
            case R.id.action_to_watchedlist:
                onAddToWatchedClicked();
                return true;
            case R.id.action_to_watchlist:
                onAddToWatchClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onAddToWatchedClicked() {
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
                        movieDbHelper.createNewMovieEntry(movie);
                    }
                };
                break;
            case R.string.watchlistState:
                currentMovie.setWatched(true);
                movieDbHelper.update(currentMovie);
                break;
        }


        if (callback != null) {
            NetworkClient client = new NetworkClient(new NetworkRequest().get(currentMovie.getId()));
            client.sendRequest(callback);
        }

        Toast.makeText(this, this.getResources().getString(R.string.movieAdd,
                currentMovie.getTitle()), Toast.LENGTH_SHORT).show();
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
                        movieDbHelper.createNewMovieEntry(gson.fromJson(response.getResponseReader(), Movie.class));
                    }
                };
                break;
        }

        if (callback != null) {
            NetworkClient client = new NetworkClient(new NetworkRequest().get(currentMovie.getId()));
            client.sendRequest(callback);
        }

        Toast.makeText(this, this.getResources().getString(R.string.movieAdd,
                currentMovie.getTitle()), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        movieId = intent.getLongExtra(BaseDao.MovieEntry._ID, -1);
        state = intent.getIntExtra(getString(R.string.state), -1);

        autoUpdateMovie();

        moviePoster = (ImageView) findViewById(R.id.movie_poster);
        rating = (TextView) findViewById(R.id.rating);
        movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieRuntime = (TextView) findViewById(R.id.movieRuntime);
        layout = (NestedScrollView) findViewById(R.id.overlay);

        movieDbHelper = MovieDbHelper.getInstance(this);
        currentMovie = movieDbHelper.readMovie(movieId);
        if (currentMovie == null) {
            loadRequestedMovie(state);
        } else {
            assignData(currentMovie, state);
        }

        initToolbar();
        slideIn();
    }

    private void loadRequestedMovie(final int state) {
        NetworkClient client = new NetworkClient(new NetworkRequest().get(movieId));
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
                        assignData(movie, state);
                    }
                });
            }
        });
    }

    private void assignData(Movie currentMovie, int state) {
        TextView movieDescription = (TextView) findViewById(R.id.movie_description);

        String description = currentMovie.getDescription();
        movieDescription.setText(
                (description == null || description.isEmpty())
                        ? getString(R.string.noDescription) : description);

        movieTitle.setText(currentMovie.getTitle());
        movieRuntime.setText(getString(R.string.runtime, currentMovie.getRuntime()));
        rating.setText(String.valueOf(currentMovie.getVoteAverage()));


        String posterUri = Constants.POSTER_URI_SMALL
                .replace("<posterName>", currentMovie.getPosterPath() != null ?
                        currentMovie.getPosterPath() : "/");
        Picasso.with(this)
                .load(posterUri)
                .error(R.drawable.placeholder_poster)
                .into(moviePoster);
    }

    private void initToolbar() {
        transparentStatusBar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        setTitleIfNeeded();
    }

    private void setTitleIfNeeded() {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
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

    @TargetApi(21)
    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark_half_translucent));
        }
    }

    private void autoUpdateMovie() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateMovie();
            }
        };

        Handler handler = new Handler();

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000);


    }


    private void updateMovie() {
        if (state != R.string.searchState) {
            NetworkClient client = new NetworkClient(new NetworkRequest().get(movieId));
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
                            assignData(movie, state);
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
    }

    private void slideIn() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.to_top);
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
