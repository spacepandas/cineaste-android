package de.cineaste.android.activity;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.MultiList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        NearbyMessageHandler handler = NearbyMessageHandler.getInstance();
        nearbyMessages = handler.getMessages();

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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onMovieSelectListener(int position) {

        NetworkClient client = new NetworkClient(new NetworkRequest().get(getResult().get(position).getId()));
        client.sendRequest(new NetworkCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccess(NetworkResponse response) {
                Gson gson = new Gson();
                final Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MovieDbHelper db = MovieDbHelper.getInstance(ResultActivity.this);
                        movie.setWatched(true);
                        if (db.readMovie(movie.getId()) != null) {
                            db.update(movie);
                        } else {
                            db.createOrUpdate(movie);
                        }
                    }
                });
            }
        });

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
