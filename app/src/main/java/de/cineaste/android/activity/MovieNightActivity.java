package de.cineaste.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.cineaste.android.R;
import de.cineaste.android.adapter.NearbyUserAdapter;
import de.cineaste.android.database.dbHelper.MovieDbHelper;
import de.cineaste.android.database.NearbyMessageHandler;
import de.cineaste.android.database.dbHelper.UserDbHelper;
import de.cineaste.android.entity.movie.Movie;
import de.cineaste.android.entity.movie.MovieDto;
import de.cineaste.android.entity.movie.NearbyMessage;
import de.cineaste.android.entity.User;
import de.cineaste.android.fragment.UserInputFragment;
import de.cineaste.android.fragment.WatchState;

public class MovieNightActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        UserInputFragment.UserNameListener {

    private static final int TTL_IN_SECONDS = 3 * 60;
    private static final String KEY_UUID = "key_uuid";

    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(TTL_IN_SECONDS).build();

    private final ArrayList<NearbyMessage> nearbyMessagesArrayList = new ArrayList<>();

    private static String getUUID(SharedPreferences sharedPreferences) {
        String uuid = sharedPreferences.getString(KEY_UUID, "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(KEY_UUID, uuid).apply();
        }
        return uuid;
    }

    private GoogleApiClient mGoogleApiClient;

    private Button startBtn;
    private TextView searchingFriends;
    private ProgressBar progressBar;
    private RecyclerView nearbyUser_rv;

    private MessageListener mMessageListener;
    private NearbyMessage localNearbyMessage;

    private NearbyUserAdapter nearbyUserAdapter;

    private User currentUser;
    private UserDbHelper userDbHelper;
    private Runnable timeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_night);
        userDbHelper = UserDbHelper.getInstance(this);
        currentUser = userDbHelper.getUser();

        initViews();

        if (currentUser == null) {
            startDialogFragment();
        } else {
            buildLocalMessage();
        }
        mMessageListener = new MyMessageListener();
        initializeTimeout();
        buildGoogleApiClient();

        timedOut();
    }

    @Override
    public void onPause() {
        super.onPause();
        nearbyUser_rv.removeCallbacks(timeOut);
    }

    private void timedOut() {
        nearbyUser_rv.removeCallbacks(timeOut);
        nearbyUser_rv.postDelayed(timeOut, 45000);
    }

    private void initializeTimeout() {
        timeOut = new Runnable() {
            @Override
            public void run() {
                Snackbar snackbar = Snackbar
                        .make(nearbyUser_rv, R.string.no_friends_found_try_again, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        };
    }

    private void initViews() {
        nearbyUser_rv = findViewById(R.id.nearbyUser_rv);
        startBtn = findViewById(R.id.start_btn);
        startBtn.setVisibility(View.GONE);
        searchingFriends = findViewById(R.id.searchingFriends);
        progressBar = findViewById(R.id.progressBar);

        nearbyUserAdapter =
                new NearbyUserAdapter(nearbyMessagesArrayList, this);

        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        nearbyUser_rv.setLayoutManager(llm);
        nearbyUser_rv.setItemAnimator(new DefaultItemAnimator());
        nearbyUser_rv.setAdapter(nearbyUserAdapter);
        initToolbar();

    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .build();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.startMovieNight);
    }

    private void buildLocalMessage() {
        MovieDbHelper watchlistDbHelper = MovieDbHelper.getInstance(this);
        List<Movie> localWatchlistMovies = watchlistDbHelper.readMoviesByWatchStatus(WatchState.WATCH_STATE);
        final List<MovieDto> localMovies = transFormMovies(localWatchlistMovies);
        localNearbyMessage = new NearbyMessage(currentUser.getUserName(), getMyUUid(), localMovies);
    }

    private void startDialogFragment() {
        new UserInputFragment().show(getSupportFragmentManager(), "");
    }


    @Override
    public void onFinishUserDialog(String userName) {
        if (!userName.isEmpty()) {
            currentUser = new User(userName);
            userDbHelper.createUser(currentUser);
        }
        buildLocalMessage();
    }

    private String getMyUUid() {
        return getUUID(getSharedPreferences(
                getApplicationContext().getPackageName(), Context.MODE_PRIVATE));
    }

    private List<MovieDto> transFormMovies(List<Movie> movies) {
        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie : movies) {
            movieDtos.add(new MovieDto(movie));
        }
        return movieDtos;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbyMessageHandler handler = NearbyMessageHandler.getInstance();
                handler.clearMessages();
                handler.addMessage(localNearbyMessage);
                handler.addMessages(nearbyMessagesArrayList);
                Intent intent = new Intent(MovieNightActivity.this, ResultActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected() && !isChangingConfigurations()) {
            unsubscribe();
            unpublish();

            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void unsubscribe() {
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(@NonNull Status status) {
                            if (!status.isSuccess()) {
                                logAndShowSnackbar("Could not unsubscribe, status = " + status);
                            }
                        }
                    });
        }
    }

    private void unpublish() {
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            Nearby.Messages.unpublish(mGoogleApiClient, NearbyMessage.newNearbyMessage(localNearbyMessage))
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(@NonNull Status status) {
                            if (!status.isSuccess()) {
                                logAndShowSnackbar("Could not unpublish, status = " + status);
                            }
                        }
                    });
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        subscribe();
        publish();
    }

    private void subscribe() {
        //trying to subscribe
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            clearDeviceList();
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new SubscribeCallback() {
                    }).build();

            Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(@NonNull Status status) {
                            if (!status.isSuccess()) {
                                logAndShowSnackbar("Could not subscribe, status = " + status);
                            }
                        }
                    });
        }
    }

    private void clearDeviceList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nearbyMessagesArrayList.clear();
                nearbyUserAdapter.notifyDataSetChanged();
            }
        });
    }

    private void publish() {
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        } else {
            PublishOptions options = new PublishOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new PublishCallback() {
                    }).build();
            Nearby.Messages.publish(mGoogleApiClient, NearbyMessage.newNearbyMessage(localNearbyMessage), options)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(@NonNull Status status) {
                            if (!status.isSuccess()) {
                                logAndShowSnackbar("Could not publish, status = " + status);
                            }
                        }
                    });
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // GoogleApiClient connection suspended
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //connection to GoogleApiClient failed
        logAndShowSnackbar("Exception while connecting to Google Play services: " +
                connectionResult.getErrorMessage());
    }

    private void logAndShowSnackbar(final String text) {
        Log.w(MovieNightActivity.class.getSimpleName(), text);
        View container = findViewById(R.id.recycler_view);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private class MyMessageListener extends MessageListener {
        @Override
        public void onFound(final Message message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!nearbyMessagesArrayList.contains(NearbyMessage.fromMessage(message))) {
                        nearbyMessagesArrayList.add(NearbyMessage.fromMessage(message));
                        if (nearbyMessagesArrayList.size() > 0) {
                            startBtn.setVisibility(View.VISIBLE);
                            nearbyUser_rv.setVisibility(View.VISIBLE);
                            searchingFriends.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                        nearbyUserAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        @Override
        public void onLost(final Message message) {
            //do not remove messages when connection lost
//			    getActivity().runOnUiThread( new Runnable() {
//                    @Override
//                    public void run() {
//                        NearbyMessage temp = NearbyMessage.fromMessage( message );
//                        int position = nearbyMessagesArrayList.indexOf( temp );
//                        nearbyMessagesArrayList.remove( temp );
//                        nearbyUserAdapter.notifyItemRemoved( position );
//                    }
//                } );
        }
    }
}
