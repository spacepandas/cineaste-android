package de.cineaste.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import de.cineaste.android.adapter.DetailViewAdapter;
import de.cineaste.android.adapter.OnBackPressedListener;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.NetworkCallback;
import de.cineaste.android.network.NetworkClient;
import de.cineaste.android.network.NetworkRequest;
import de.cineaste.android.network.NetworkResponse;

import static de.cineaste.android.viewholder.StateSearchViewHolder.OnAddToListInSearchState;
import static de.cineaste.android.viewholder.StateWatchListViewHolder.OnMovieStateOnWatchListChanged;
import static de.cineaste.android.viewholder.StateWatchedListViewHolder.OnMovieRemovedFromWatchedList;


public class MovieDetailActivity extends AppCompatActivity implements OnBackPressedListener, OnAddToListInSearchState, OnMovieRemovedFromWatchedList, OnMovieStateOnWatchListChanged {

	private final Gson gson = new Gson();
	private ImageView moviePoster;
	private SwipeRefreshLayout swipeRefreshLayout;
	private MovieDbHelper movieDbHelper;
	private long movieId;
	private Movie currentMovie;
	private TextView rating;

	@Override
	public void onBackPressedListener() {
		onBackPressed();
	}

	@Override
	public void onBackPressedListener(Movie movie) {
		Toast.makeText(this, this.getResources().getString(R.string.movieAdd,
				movie.getTitle()), Toast.LENGTH_SHORT).show();
		onBackPressed();
	}

	@Override
	public void onAddToList(Movie currentMovie, int viewID) {
		NetworkCallback callback = null;
		switch (viewID) {
			case R.id.addToWatchedList:
				callback = new NetworkCallback() {
					@Override
					public void onFailure() {

					}

					@Override
					public void onSuccess(NetworkResponse response) {
						Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
						movie.setWatched(true);
						movieDbHelper.createNewMovieEntry(movie);
					}
				};
				break;
			case R.id.remove:
				movieDbHelper.deleteMovieFromWatchlist(currentMovie);
				break;
			case R.id.addToWatchList:
				callback = new NetworkCallback() {
					@Override
					public void onFailure() {

					}

					@Override
					public void onSuccess(NetworkResponse response) {
						movieDbHelper.createNewMovieEntry(gson.fromJson(response.getResponseReader(), Movie.class));
					}
				};
				break;
		}
		if (callback != null) {
			NetworkClient client = new NetworkClient(new NetworkRequest().get(currentMovie.getId()));
			client.sendRequest(callback);
		}

	}

	@Override
	public void movieRemoved(Movie movie) {
		movieDbHelper.deleteMovieFromWatchlist(movie);
	}

	@Override
	public void movieStateOnWatchListChanged(Movie movie, int viewId) {
		switch (viewId) {
			case R.id.addToWatchedList:
				movie.setWatched(true);
				movieDbHelper.update(movie);
				break;
			case R.id.remove:
				movieDbHelper.deleteMovieFromWatchlist(movie);
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				//overridePendingTransition( R.anim.fade_out, R.anim.fade_in );
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_detail);

		Intent intent = getIntent();
		movieId = intent.getLongExtra(BaseDao.MovieEntry._ID, -1);
		final int state = intent.getIntExtra(getString(R.string.state), -1);

		initToolbar();

		moviePoster = (ImageView) findViewById(R.id.movie_poster);
		rating = (TextView) findViewById(R.id.rating);

		initSwipeRefresh(state);
		movieDbHelper = MovieDbHelper.getInstance(this);
		currentMovie = movieDbHelper.readMovie(movieId);
		if (currentMovie == null) {
			NetworkClient client = new NetworkClient(new NetworkRequest().get(movieId));
			client.sendRequest(new NetworkCallback() {
				@Override
				public void onFailure() {

				}

				@Override
				public void onSuccess(NetworkResponse response) {
					final Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							assignData(movie, state);
						}
					});
				}
			});
		} else {
			assignData(currentMovie, state);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		animateTriangle();
	}

	private void assignData(Movie currentMovie, int state) {
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		if (recyclerView != null) {
			recyclerView.setLayoutManager(llm);
			recyclerView.setItemAnimator(new DefaultItemAnimator());
		}

		RecyclerView.Adapter adapter = new DetailViewAdapter(currentMovie, state, this, this, this, this);
		if (recyclerView != null)
			recyclerView.setAdapter(adapter);

		String posterUri = Constants.POSTER_URI_SMALL
				.replace("<posterName>", currentMovie.getPosterPath() != null ?
						currentMovie.getPosterPath() : "/");
		Picasso.with(this)
				.load(posterUri)
				.error(R.drawable.placeholder_poster)
				.into(moviePoster);

		rating.setText(String.valueOf(currentMovie.getVoteAverage()));
	}

	private void initToolbar() {
		transparentStatusBar();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
			actionBar.setDisplayHomeAsUpEnabled(true);

		setTitleIfNeeded();
	}

	private void setTitleIfNeeded() {
		final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
		AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
		appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
			boolean isShow = true;
			int scrollRange = -1;

			@Override
			public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
				if (scrollRange == -1) {
					scrollRange = appBarLayout.getTotalScrollRange();
				}
				if (scrollRange + verticalOffset == 0) {
					collapsingToolbarLayout.setTitle(getString(R.string.app_name));
					isShow = true;
				} else if (isShow) {
					collapsingToolbarLayout.setTitle(" ");
					isShow = false;
				}
			}
		});
	}

	@TargetApi(21)
	private void transparentStatusBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark_half_translucent));
		}
	}

	private void initSwipeRefresh(final int state) {
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
		swipeRefreshLayout.setOnRefreshListener(
				new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						updateMovie(state);
					}
				});
	}

	private void updateMovie(final int state) {
		if (state != R.string.searchState) {
			NetworkClient client = new NetworkClient(new NetworkRequest().get(movieId));
			client.sendRequest(new NetworkCallback() {
				@Override
				public void onFailure() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showNetworkError();
							swipeRefreshLayout.setRefreshing(false);
						}
					});
				}

				@Override
				public void onSuccess(NetworkResponse response) {
					final Movie movie = gson.fromJson(response.getResponseReader(), Movie.class);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							assignData(movie, state);
							updateMovieDetails(movie);
							movieDbHelper.createOrUpdate(currentMovie);
							swipeRefreshLayout.setRefreshing(false);
						}
					});
				}
			});
		} else {
			swipeRefreshLayout.setRefreshing(false);
		}
	}

	private void updateMovieDetails(Movie movie) {
		currentMovie.setTitle(movie.getTitle());
		currentMovie.setRuntime(movie.getRuntime());
		currentMovie.setVoteAverage(movie.getVoteAverage());
		currentMovie.setVoteCount(movie.getVoteCount());
		currentMovie.setDescription(movie.getDescription());
		currentMovie.setPosterPath(movie.getPosterPath());
	}

	private void animateTriangle() {
		View triangle = findViewById(R.id.triangle);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.count_circle);
		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
		fadeIn.setDuration(1000);
		if (triangle != null && layout != null) {
			triangle.startAnimation(fadeIn);
			layout.startAnimation(fadeIn);
		}
	}

	private void showNetworkError() {
		Snackbar snackbar = Snackbar
				.make(swipeRefreshLayout, R.string.noInternet, Snackbar.LENGTH_LONG);
		snackbar.show();
	}
}
