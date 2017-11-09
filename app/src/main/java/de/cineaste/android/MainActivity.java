package de.cineaste.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import de.cineaste.android.database.UserDbHelper;
import de.cineaste.android.entity.User;
import de.cineaste.android.fragment.BaseMovieListFragment;
import de.cineaste.android.fragment.MovieNightFragment;
import de.cineaste.android.fragment.UserInputFragment;
import de.cineaste.android.fragment.WatchState;

public class MainActivity extends AppCompatActivity implements UserInputFragment.UserNameListener {

	private FragmentManager fm;
	private UserDbHelper userDbHelper;
	private static User currentUser;

	private DrawerLayout drawerLayout;

	public static void replaceFragment(FragmentManager fm, Fragment fragment) {
		fm.beginTransaction()
				.replace(
						R.id.content_container,
						fragment, fragment.getClass().getName())
				.addToBackStack(null)
				.commit();
	}

	public static void replaceFragmentPopBackStack(FragmentManager fm, Fragment fragment) {
		fm.popBackStack();
		replaceFragment(fm, fragment);
	}

	public static void startMovieNight(FragmentManager fm) {
		if (currentUser != null) {
			replaceFragment(fm, new MovieNightFragment());
		} else {
			startDialogFragment(fm, new UserInputFragment());
		}
	}

	@Override
	public void onBackPressed() {
		if( drawerLayout.isDrawerOpen( GravityCompat.START ) ) {
			drawerLayout.closeDrawer( GravityCompat.START );
		} else {
			if (fm.getBackStackEntryCount() > 1)
				super.onBackPressed();
			else
				finish();
		}

	}

	@Override
	public boolean onSupportNavigateUp() {
		if (fm.getBackStackEntryCount() > 1)
			fm.popBackStack();
		else
			drawerLayout.openDrawer(GravityCompat.START);

		return false;
	}

	@Override
	public void onFinishUserDialog(String userName) {
		if (!userName.isEmpty()) {
			currentUser = new User(userName);
			userDbHelper.createUser(currentUser);
			MainActivity.startMovieNight(fm);
		}
	}

	@Override
	public void onRequestPermissionsResult(
			int requestCode,
			@NonNull String permissions[],
			@NonNull int grantResults[]) {
		switch (requestCode) {
			default:
				break;
			case 1:
				if (grantResults.length > 0 && grantResults[0] !=
						PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(
							this,
							R.string.missing_permission,
							Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fm = getSupportFragmentManager();

		initToolbar();

		checkPermissions();
		userDbHelper = UserDbHelper.getInstance(this);

		currentUser = userDbHelper.getUser();

		if (savedInstanceState == null) {
			replaceFragment(fm, getBaseWatchlistFragment(WatchState.WATCH_STATE));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MovieNightFragment movieNightFragment = (MovieNightFragment) fm.findFragmentByTag(MovieNightFragment.class.getName());
		movieNightFragment.finishedResolvingNearbyPermissionError();
		if (requestCode == 1001) {
			if (resultCode == Activity.RESULT_OK) {

				movieNightFragment.start();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				movieNightFragment.stop();
			} else {
				Toast.makeText(this, "Failed to resolve error with code " + resultCode,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void checkPermissions() {
		String storage = Manifest.permission.READ_EXTERNAL_STORAGE;
		if (ContextCompat.checkSelfPermission(this, storage) == PackageManager.PERMISSION_GRANTED)
			return;


		ActivityCompat.requestPermissions(this, new String[]{storage}, 1);
	}

	private void initToolbar() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
			actionBar.setDisplayHomeAsUpEnabled(true);

		NavigationView navigationView = findViewById(R.id.navigation_view);
		navigationView.setNavigationItemSelectedListener(new CustomDrawerClickListener());

		drawerLayout = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
				this, drawerLayout, R.string.open, R.string.close
		);
		drawerToggle.setDrawerIndicatorEnabled(true);
		drawerLayout.addDrawerListener(drawerToggle);
		drawerToggle.syncState();
	}

	private static void startDialogFragment(FragmentManager fragmentManager, DialogFragment fragment) {
		fragment.show(fragmentManager, "");
	}

	private class CustomDrawerClickListener implements NavigationView.OnNavigationItemSelectedListener {
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch (item.getItemId()) {
				case R.id.show_watchlist:
					BaseMovieListFragment watchlistFragment = getBaseWatchlistFragment(WatchState.WATCH_STATE);
					replaceFragmentPopBackStack(fm, watchlistFragment);
					break;
				case R.id.show_watchedlist:
					BaseMovieListFragment watchedlistFragment = getBaseWatchlistFragment(WatchState.WATCHED_STATE);
					replaceFragmentPopBackStack(fm, watchedlistFragment);
					break;
			}
			drawerLayout.closeDrawer(GravityCompat.START);
			return true;
		}
	}

	@NonNull
	private BaseMovieListFragment getBaseWatchlistFragment(WatchState state) {
		BaseMovieListFragment watchlistFragment = new BaseMovieListFragment();
		Bundle bundle = new Bundle();
		bundle.putString(
				WatchState.WATCH_STATE_TYPE.name(),
				state.name());
		watchlistFragment.setArguments(bundle);
		return watchlistFragment;
	}
}
