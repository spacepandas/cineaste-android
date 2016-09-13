package de.cineaste.android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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

import com.squareup.picasso.Picasso;

import de.cineaste.android.adapter.DetailViewAdapter;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.TheMovieDb;
import de.cineaste.android.receiver.NetworkChangeReceiver;
import de.cineaste.android.viewholder.HeadViewHolder;

public class MovieDetailActivity extends AppCompatActivity implements HeadViewHolder.OnBackPressedListener {

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
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case android.R.id.home:
                onBackPressed();
                //overridePendingTransition( R.anim.fade_out, R.anim.fade_in );
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_movie_detail );

        Intent intent = getIntent();
        movieId = intent.getLongExtra( BaseDao.MovieEntry._ID, -1 );
        final int state = intent.getIntExtra( getString( R.string.state ), -1 );

        initToolbar();

        moviePoster = (ImageView) findViewById( R.id.movie_poster );
        rating = (TextView) findViewById(R.id.rating);

        initSwipeRefresh( state );
        movieDbHelper = MovieDbHelper.getInstance( this );
        currentMovie = movieDbHelper.readMovie( movieId );
        if( currentMovie == null ) {
            if( NetworkChangeReceiver.getInstance().isConnected ) {
                TheMovieDb theMovieDb = new TheMovieDb();
                theMovieDb.fetchMovie( movieId, getResources().getString( R.string.language_tag ),
                        new TheMovieDb.OnFetchMovieResultListener() {
                    @Override
                    public void onFetchMovieResultListener( Movie movie ) {
                        assignData( movie, state );
                    }
                } );
            }
        } else {
            assignData( currentMovie, state );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        animateTriangle();
    }

    private void assignData(Movie currentMovie, int state ) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation( LinearLayoutManager.VERTICAL );
        if (recyclerView != null) {
            recyclerView.setLayoutManager(llm);
            recyclerView.setItemAnimator( new DefaultItemAnimator() );
        }

        RecyclerView.Adapter adapter = new DetailViewAdapter(this, currentMovie, state, new HeadViewHolder.OnBackPressedListener() {
            @Override
            public void onBackPressedListener() {
                onBackPressed();
            }
        });
        if (recyclerView != null)
            recyclerView.setAdapter(adapter);

        String posterUri = Constants.POSTER_URI_SMALL
                .replace( "<posterName>", currentMovie.getPosterPath() != null ?
                        currentMovie.getPosterPath() : "/" );
        Picasso.with( this )
                .load( posterUri )
                .error( R.drawable.placeholder_poster )
                .into( moviePoster );

        rating.setText(String.valueOf(currentMovie.getVoteAverage()));
    }

    private void initToolbar() {
        transparentStatusBar();
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null )
            actionBar.setDisplayHomeAsUpEnabled( true );

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
            collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this,R.color.toolbar_text));
        }
    }

    @TargetApi(21)
    private void transparentStatusBar() {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark_half_translucent));
    }

    private void initSwipeRefresh( final int state) {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById( R.id.swipe_refresh );
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateMovie(state);
                    }
                } );
    }

    private void updateMovie( final int state ) {
        if( NetworkChangeReceiver.getInstance().isConnected && state != R.string.searchState ) {
            TheMovieDb theMovieDb = new TheMovieDb();
            theMovieDb.fetchMovie( movieId, getResources().getString( R.string.language_tag ),
                    new TheMovieDb.OnFetchMovieResultListener() {
                @Override
                public void onFetchMovieResultListener( Movie movie ) {
                    assignData( movie, state );
                    updateMovieDetails( movie );
                    movieDbHelper.createOrUpdate( currentMovie );
                    swipeRefreshLayout.setRefreshing( false );
                }
            } );
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateMovieDetails( Movie movie ) {
        currentMovie.setTitle( movie.getTitle() );
        currentMovie.setRuntime( movie.getRuntime() );
        currentMovie.setVoteAverage( movie.getVoteAverage() );
        currentMovie.setVoteCount( movie.getVoteCount() );
        currentMovie.setDescription( movie.getDescription() );
        currentMovie.setPosterPath( movie.getPosterPath() );
    }

    private void animateTriangle() {
        View triangle = findViewById(R.id.triangle);
        RelativeLayout layout = (RelativeLayout) findViewById( R.id.count_circle );
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.setDuration(1000);
        if (triangle != null && layout != null) {
            triangle.startAnimation(fadeIn);
            layout.startAnimation( fadeIn );
        }
    }
}