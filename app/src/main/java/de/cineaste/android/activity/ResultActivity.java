package de.cineaste.android.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.util.DateAwareGson;
import de.cineaste.android.util.MultiList;
import de.cineaste.android.R;
import de.cineaste.android.adapter.ResultAdapter;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.database.NearbyMessageHandler;
import de.cineaste.android.entity.MatchingResult;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.entity.MovieDto;
import de.cineaste.android.entity.NearbyMessage;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;

public class ResultActivity extends AppCompatActivity implements ResultAdapter.OnMovieSelectListener {

    private List<NearbyMessage> nearbyMessages;
    private MovieDbHelper movieDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        NearbyMessageHandler handler = NearbyMessageHandler.getInstance();
        nearbyMessages = handler.getMessages();

        movieDbHelper = MovieDbHelper.getInstance(this);

        initToolbar();

        RecyclerView result = findViewById(R.id.result_list);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        result.setLayoutManager(llm);
        result.setItemAnimator(new DefaultItemAnimator());

        ResultAdapter resultAdapter = new ResultAdapter(
                getResult(),
                this);
        result.setAdapter(resultAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.result);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onMovieSelectListener(int position) {

        long selectedMovieId = getResult().get(position).getId();
        Movie selectedMovie = movieDbHelper.readMovie(selectedMovieId);

        if (selectedMovie == null) {
            NetworkClient client = new NetworkClient(new NetworkRequest(getResources()).get(getResult().get(position).getId()));
            client.sendRequest(new NetworkCallback() {
                @Override
                public void onFailure() {

                }

                @Override
                public void onSuccess(NetworkResponse response) {
                    Gson gson = new DateAwareGson().getGson();
                    final Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateMovie(movie);
                        }
                    });
                }
            });
        } else {
            updateMovie(selectedMovie);
        }
    }

    private void updateMovie( Movie movie) {
        movie.setWatched(true);
        movieDbHelper.createOrUpdate(movie);
    }

    private ArrayList<MatchingResult> getResult() {
        ArrayList<MatchingResult> results = new ArrayList<>();
        MultiList multiList = new MultiList();
        multiList.addAll(getMovies());

        for (MultiList.MultiListEntry multiListEntry : multiList.getMovieList()) {
            results.add(new MatchingResult(multiListEntry.getMovieDto(), multiListEntry.getCounter()));
        }

        return results;
    }

    private ArrayList<MovieDto> getMovies() {
        ArrayList<MovieDto> movies = new ArrayList<>();

        for (NearbyMessage current : nearbyMessages) {
            movies.addAll(current.getMovies());
        }

        return movies;
    }
}
