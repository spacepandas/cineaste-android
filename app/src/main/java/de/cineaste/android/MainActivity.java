package de.cineaste.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import de.cineaste.android.database.UserDbHelper;
import de.cineaste.android.entity.User;
import de.cineaste.android.fragment.MovieNightFragment;
import de.cineaste.android.fragment.UserInputFragment;
import de.cineaste.android.fragment.ViewPagerFragment;
import de.cineaste.android.receiver.NetworkChangeReceiver;

public class MainActivity extends AppCompatActivity implements UserInputFragment.UserNameListener {

	private FragmentManager fm;
	private UserDbHelper userDbHelper;
	private static User currentUser;

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
		if (fm.getBackStackEntryCount() > 1)
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
			replaceFragment(fm, new ViewPagerFragment());
		}

		registerNetworkChangeReceiver();
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			getBaseContext().unregisterReceiver(NetworkChangeReceiver.getInstance());
		} catch (IllegalArgumentException e) {
			//die silently
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
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

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
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(
					fm.getBackStackEntryCount() > 1
			);
		}
	}

	private void registerNetworkChangeReceiver() {
		IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		getBaseContext().registerReceiver(NetworkChangeReceiver.getInstance(), networkFilter);
	}

	private static void startDialogFragment(FragmentManager fragmentManager, DialogFragment fragment) {
		fragment.show(fragmentManager, "");
	}
}
