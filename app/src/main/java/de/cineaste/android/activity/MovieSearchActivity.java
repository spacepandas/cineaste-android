package de.cineaste.android.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.adapter.movie.MovieSearchQueryAdapter;
import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dbHelper.MovieDbHelper;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;

public class MovieSearchActivity extends AbstractSearchActivity implements  MovieSearchQueryAdapter.OnMovieStateChange {

    private final MovieDbHelper db = MovieDbHelper.getInstance(this);
    private MovieSearchQueryAdapter movieQueryAdapter;

    @Override
    @NonNull
    protected Intent getIntentForDetailActivity(long itemId) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(BaseDao.MovieEntry._ID, itemId);
        intent.putExtra(this.getString(R.string.state), R.string.searchState);
        return intent;
    }

    @Override
    public void onMovieStateChangeListener(final Movie movie, int viewId, final int index) {
        NetworkCallback callback;
        switch (viewId) {
            case R.id.to_watchlist_button:
                callback = new NetworkCallback() {
                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                movieAddError(movie, index);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(NetworkResponse response) {
                        db.createOrUpdate(gson.fromJson(response.getResponseReader(), Movie.class));

                    }
                };
                break;
            case R.id.history_button:
                callback = new NetworkCallback() {
                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                movieAddError(movie, index);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(NetworkResponse response) {
                        Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
                        movie.setWatched(true);
                        db.createOrUpdate(movie);
                    }
                };
                break;
            default:
                callback = null;
                break;
        }
        if (callback != null) {
            movieQueryAdapter.removeMovie(index);
            NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getMovie(movie.getId()));
            client.sendRequest(callback);
        }

    }

    private void movieAddError(Movie movie, int index) {
        Snackbar snackbar = Snackbar
                .make(recyclerView, R.string.could_not_add_movie, Snackbar.LENGTH_LONG);
        snackbar.show();
        movieQueryAdapter.addMovie(movie, index);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initAdapter() {
        movieQueryAdapter = new MovieSearchQueryAdapter(this, this);
    }

    @Override
    protected RecyclerView.Adapter getListAdapter() {
        return movieQueryAdapter;
    }

    @Override
    protected void getSuggestions() {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).getUpcomingMovies());
        client.sendRequest(getNetworkCallback());
    }

    @Override
    protected void searchRequest( String searchQuery) {
        NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).searchMovie(searchQuery));
        client.sendRequest(getNetworkCallback());
    }

    @Override
    protected Type getListType() {
        return new TypeToken<List<Movie>>() {
        }.getType();
    }

    @Override
    @NonNull
    protected Runnable getRunnable(final String json, final Type listType) {
        return new Runnable() {
            @Override
            public void run() {
                final List<Movie> movies = gson.fromJson(json, listType);
                movieQueryAdapter.addMovies(movies);
                progressBar.setVisibility(View.GONE);
            }
        };
    }
}
