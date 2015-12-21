package de.cineaste.android.fragment;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.MainActivity;
import de.cineaste.android.R;
import de.cineaste.android.adapter.NearbyUserAdapter;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.entity.MovieDto;
import de.cineaste.android.entity.NearbyMessage;
import de.cineaste.android.entity.User;
import de.cineaste.android.persistence.MovieDbHelper;
import de.cineaste.android.persistence.NearbyMessageHandler;
import de.cineaste.android.persistence.UserDbHelper;

public class MovieNightFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static MovieNightFragment instance;
    private static final String TAG = "movie_night_fragment";

    private String userName;

    private static final Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(180).build();

    private Button startBtn;
    private RecyclerView nearbyUser_rv;

    private NearbyUserAdapter nearbyUserAdapter;

    private final ArrayList<NearbyMessage> nearbyMessagesArrayList = new ArrayList<>();
    private GoogleApiClient googleApiClient;
    private Message deviceInfoMessage;
    private MessageListener messageListener;


    private boolean mResolvingNearbyPermissionError = false;

    private MovieDbHelper watchlistDbHelper;

    public static MovieNightFragment getInstance() {
        if( instance == null )
            instance =new MovieNightFragment();
        return instance;
    }

    private MovieNightFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        watchlistDbHelper = MovieDbHelper.getInstance(getActivity());
        UserDbHelper userDbHelper = UserDbHelper.getInstance(getActivity());
        User currentUser = userDbHelper.getUser();
        userName = (currentUser != null) ? currentUser.getUserName() : InstanceID.getInstance( getActivity().getApplicationContext() ).getId() ;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_night, container, false);

        nearbyUser_rv = (RecyclerView) view.findViewById(R.id.nearbyUser_rv);
        startBtn = (Button) view.findViewById(R.id.start_btn);

        nearbyUserAdapter = new NearbyUserAdapter(nearbyMessagesArrayList, R.layout.card_nearby_user, getActivity());

        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        nearbyUser_rv.setLayoutManager(llm);
        nearbyUser_rv.setItemAnimator(new DefaultItemAnimator());
        nearbyUser_rv.setAdapter(nearbyUserAdapter);



        messageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nearbyMessagesArrayList.add(NearbyMessage.fromMessage(message));
                        nearbyUserAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onLost(final Message message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        NearbyMessage temp = NearbyMessage.fromMessage(message);
                        int position = nearbyMessagesArrayList.indexOf(temp);
                        nearbyMessagesArrayList.remove(temp);
                        nearbyUserAdapter.notifyItemRemoved(position);
                    }
                });
            }
        };

        return view;
    }


    private void clearDeviceList() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nearbyMessagesArrayList.clear();
                nearbyUserAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        final String deviceId = InstanceID.getInstance( getActivity().getApplicationContext() ).getId();
        List<Movie> localWatchlistMovies = watchlistDbHelper.readMoviesByWatchStatus(false);
        final List<MovieDto> localMovies = transFormMovies(localWatchlistMovies);
        deviceInfoMessage = NearbyMessage.newNearbyMessage(deviceId, localMovies, userName);

        googleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addApi( Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();


        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NearbyMessageHandler handler = NearbyMessageHandler.getInstance();
                NearbyMessage localNearbyMessage = new NearbyMessage(userName, deviceId, localMovies);
                handler.addMessage(localNearbyMessage);
                handler.addMessages(nearbyMessagesArrayList);
                MainActivity.replaceFragment( getFragmentManager(), new SearchFragment() );
            }
        });


    }

    @Override
    public void onStop() {
        if (googleApiClient.isConnected() && !getActivity().isChangingConfigurations()) {
            unsubscribe();
            unpublish();

            googleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i( TAG, "GoogleApiClient connected" );
        subscribe();
        publish();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended: "
                + connectionSuspendedCauseToString(cause));
    }

    private static String connectionSuspendedCauseToString(int cause) {
        switch (cause) {
            case CAUSE_NETWORK_LOST:
                return "CAUSE_NETWORK_LOST";
            case CAUSE_SERVICE_DISCONNECTED:
                return "CAUSE_SERVICE_DISCONNECTED";
            default:
                return "CAUSE_UNKNOWN: " + cause;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "connection to GoogleApiClient failed");
    }


    private void subscribe() {
        Log.i(TAG, "trying to subscribe");
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            clearDeviceList();
            SubscribeOptions options = new SubscribeOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new SubscribeCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
                            Log.i(TAG, "no longer subscribing");
                        }
                    }).build();

            Nearby.Messages.subscribe(googleApiClient, messageListener, options)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "subscribed successfully");
                            } else {
                                Log.i(TAG, "could not subscribe");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    private void unsubscribe() {
        Log.i(TAG, "trying to unsubscribe");
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Nearby.Messages.unsubscribe(googleApiClient, messageListener)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "unsubscribed successfully");
                            } else {
                                Log.i(TAG, "could not unsubscribe");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    private void publish() {
        Log.i(TAG, "trying to publish");
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            PublishOptions options = new PublishOptions.Builder()
                    .setStrategy(PUB_SUB_STRATEGY)
                    .setCallback(new PublishCallback() {
                        @Override
                        public void onExpired() {
                            super.onExpired();
                            Log.i(TAG, "no longer publishing");
                        }
                    }).build();

            Nearby.Messages.publish(googleApiClient, deviceInfoMessage, options)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "published successfully");
                            } else {
                                Log.i(TAG, "could not publish");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    private void unpublish() {
        Log.i(TAG, "trying to unpublish");
        if (!googleApiClient.isConnected()) {
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Nearby.Messages.unpublish(googleApiClient, deviceInfoMessage)
                    .setResultCallback(new ResultCallback<Status>() {

                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "unpublished successfully");
                            } else {
                                Log.i(TAG, "could not unpublish");
                                handleUnsuccessfulNearbyResult(status);
                            }
                        }
                    });
        }
    }

    private void handleUnsuccessfulNearbyResult(Status status) {
        Log.i(TAG, "processing error, status = " + status);
        if (status.getStatusCode() == NearbyMessagesStatusCodes.APP_NOT_OPTED_IN) {
            if (!mResolvingNearbyPermissionError) {
                try {
                    mResolvingNearbyPermissionError = true;
                    status.startResolutionForResult(getActivity(), 1001);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (status.getStatusCode() == ConnectionResult.NETWORK_ERROR) {
                Toast.makeText( getActivity().getApplicationContext(),
                        "No connectivity, cannot proceed. Fix in 'Settings' and try again.",
                        Toast.LENGTH_LONG ).show();
            } else {
                // To keep things simple, pop a toast for all other error messages.
                Toast.makeText(getActivity().getApplicationContext(), "Unsuccessful: " +
                        status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    public void finishedResolvingNearbyPermissionError() {
        mResolvingNearbyPermissionError = false;
    }

    public void start() {
        publish();
        subscribe();
    }

    public void stop() {
        unpublish();
        unsubscribe();
    }


    private ArrayList<String> addNames() {
        ArrayList<String> strings = new ArrayList<>();
        for (NearbyMessage current : nearbyMessagesArrayList) {
            strings.add(current.getUserName());
        }
        return strings;
    }

    private List<MovieDto> transFormMovies(List<Movie> movies) {
        List<MovieDto> movieDtos = new ArrayList<>();
        for (Movie movie : movies) {
            movieDtos.add(new MovieDto(movie));
        }
        return movieDtos;
    }


}
