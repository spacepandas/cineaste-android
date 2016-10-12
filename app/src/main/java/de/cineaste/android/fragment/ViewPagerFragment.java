package de.cineaste.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import de.cineaste.android.MainActivity;
import de.cineaste.android.R;
import de.cineaste.android.adapter.WatchlistViewPagerAdapter;
import de.cineaste.android.database.ExportService;
import de.cineaste.android.database.ImportService;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;

public class ViewPagerFragment extends Fragment {

	private ViewPager viewPager;
	private MovieDbHelper movieDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

		movieDbHelper = MovieDbHelper.getInstance(getActivity());

		PagerAdapter pagerAdapter = new WatchlistViewPagerAdapter(getChildFragmentManager(), getActivity());

		viewPager = (ViewPager) view.findViewById(R.id.basewatchlist_pager);
		viewPager.setAdapter(pagerAdapter);

		FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.replaceFragment(getFragmentManager(), new SearchFragment());
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				View view = getView();
//                ensures that the keyboard disappears when changing tabs
				if (view != null) {
					InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}

			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.start_movie_night, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.startMovieNight:
				MainActivity.startMovieNight(getFragmentManager());
				break;
			case R.id.exportMovies:
				exportMovies();
				break;
			case R.id.importMovies:
				importMovies();
				break;
			case R.id.about:
				MainActivity.replaceFragment(getFragmentManager(), new AboutFragment());
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void exportMovies() {
		List<Movie> movies = movieDbHelper.readAllMovies();
		ExportService.exportMovies(movies);
		Snackbar snackbar = Snackbar
				.make(viewPager, R.string.successfulExport, Snackbar.LENGTH_SHORT);
		snackbar.show();
	}

	private void importMovies() {
		List<Movie> movies = ImportService.importMovies();
		int snackBarMessage;
		if (movies.size() != 0) {
			for (Movie current : movies) {
				movieDbHelper.createOrUpdate(current);
			}
			snackBarMessage = R.string.successfulImport;

		} else {
			snackBarMessage = R.string.unsuccessfulImport;
		}
		Snackbar snackbar = Snackbar
				.make(viewPager, snackBarMessage, Snackbar.LENGTH_SHORT);
		snackbar.show();
	}
}