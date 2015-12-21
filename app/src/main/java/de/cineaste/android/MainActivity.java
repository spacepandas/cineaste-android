package de.cineaste.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import de.cineaste.android.entity.User;
import de.cineaste.android.fragment.MovieNightFragment;
import de.cineaste.android.fragment.UserInputFragment;
import de.cineaste.android.fragment.ViewPagerFragment;
import de.cineaste.android.persistence.UserDbHelper;

public class MainActivity extends AppCompatActivity implements UserInputFragment.UserNameListener {

    private FragmentManager fm;
    private UserDbHelper userDbHelper;
    private static User currentUser;

    public static void replaceFragment( FragmentManager fm, Fragment fragment ) {
        fm.beginTransaction()
                .replace(
                        R.id.content_container,
                        fragment )
                .addToBackStack( null )
                .commit();
    }

    public static void replaceFragmentPopBackStack( FragmentManager fm, Fragment fragment ) {
        fm.popBackStack();
        replaceFragment( fm, fragment );
    }

    public static void startUserInputDialog( FragmentManager fragmentManager ) {
        UserInputFragment userInputFragment = UserInputFragment.newInstance();
        userInputFragment.show( fragmentManager, "" );
    }

    public static void startMovieNight( FragmentManager fm ) {
        if( currentUser != null ) {
            fm.beginTransaction()
                    .replace(
                            R.id.content_container,
                            MovieNightFragment.getInstance() )
                    .addToBackStack( null )
                    .commit();
        } else {
            startUserInputDialog( fm );
        }

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
            fm.beginTransaction()
                    .replace( R.id.content_container, new ViewPagerFragment() )
                    .commit();
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
        MovieNightFragment movieNightFragment = MovieNightFragment.getInstance();
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
                    fm.getBackStackEntryCount() > 0
            );
        }
    }
}
