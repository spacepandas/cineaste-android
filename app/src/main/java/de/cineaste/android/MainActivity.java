package de.cineaste.android;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import de.cineaste.android.receiver.NetworkChangeReceiver;
import de.cineaste.android.entity.User;
import de.cineaste.android.fragment.MovieNightFragment;
import de.cineaste.android.fragment.UserInputFragment;
import de.cineaste.android.fragment.ViewPagerFragment;
import de.cineaste.android.database.UserDbHelper;

public class MainActivity extends AppCompatActivity implements UserInputFragment.UserNameListener {

    private FragmentManager fm;
    private UserDbHelper userDbHelper;
    private static User currentUser;

    public static void replaceFragment( FragmentManager fm, Fragment fragment ) {
        fm.beginTransaction()
                .replace(
                        R.id.content_container,
                        fragment, fragment.getClass().getName() )
                .addToBackStack( null )
                .commit();
    }

    public static void replaceSharedElementsFragment( FragmentManager fm, Fragment fragment, View view, String name ) {
        fm.beginTransaction()
                .addSharedElement( view, name )
                .replace(
                        R.id.content_container,
                        fragment, fragment.getClass().getName() )
                .addToBackStack( null )
                .commit();
    }

    public static void replaceFragmentPopBackStack( FragmentManager fm, Fragment fragment ) {
        fm.popBackStack();
        replaceFragment( fm, fragment );
    }

    private static void startDialogFragment( FragmentManager fragmentManager, DialogFragment fragment ) {
        fragment.show( fragmentManager, "" );
    }

    public static void startMovieNight( FragmentManager fm ) {
        if( currentUser != null ) {
            replaceFragment( fm, new MovieNightFragment() );
        } else {
            startDialogFragment( fm, new UserInputFragment() );
        }
    }

    @Override
    public void onBackPressed() {
        if( fm.getBackStackEntryCount() > 1 )
            super.onBackPressed();
        else
            finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        fm.popBackStack();

        return false;
    }

    @Override
    public void onFinishUserDialog( String userName ) {
        if( !userName.isEmpty() ) {
            currentUser = new User( userName );
            userDbHelper.createUser( currentUser );
            MainActivity.startMovieNight( fm );
        }
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        fm = getSupportFragmentManager();

        initToolbar();

        userDbHelper = UserDbHelper.getInstance( this );

        currentUser = userDbHelper.getUser();

        if( savedInstanceState == null ) {
            replaceFragment( fm, new ViewPagerFragment() );
        }

        registerNetworkChangeReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            getBaseContext().unregisterReceiver( NetworkChangeReceiver.getInstance() );
        } catch ( IllegalArgumentException e ) {
            //die silently
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        MovieNightFragment movieNightFragment = (MovieNightFragment) fm.findFragmentByTag( MovieNightFragment.class.getName() );
        movieNightFragment.finishedResolvingNearbyPermissionError();
        if( requestCode == 1001 ) {
            if( resultCode == Activity.RESULT_OK ) {

                movieNightFragment.start();
            } else if( resultCode == Activity.RESULT_CANCELED ) {
                movieNightFragment.stop();
            } else {
                Toast.makeText( this, "Failed to resolve error with code " + resultCode,
                        Toast.LENGTH_LONG ).show();
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        fm.addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        canBack();
                    }
                }
        );
        canBack();
    }

    private void canBack() {
        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null ) {
            actionBar.setDisplayHomeAsUpEnabled(
                    fm.getBackStackEntryCount() > 1
            );
        }
    }

    private void registerNetworkChangeReceiver() {
        IntentFilter networkFilter = new IntentFilter();
        networkFilter.addAction( ConnectivityManager.CONNECTIVITY_ACTION );
        getBaseContext().registerReceiver( NetworkChangeReceiver.getInstance(), networkFilter );
    }
}
