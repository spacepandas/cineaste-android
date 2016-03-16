package de.cineaste.android;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.TheMovieDb;
import de.cineaste.android.receiver.NetworkChangeReceiver;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView movieTitle;
    private TextView movieRuntime;
    private TextView movieVote;
    private TextView movieDescription;
    private ImageView moviePoster;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MovieDbHelper movieDbHelper;
    private long movieId;
    private Movie currentMovie;
    private MovieDbHelper db;
    private TheMovieDb theMovieDb;

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
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.watched_button:
                theMovieDb.fetchMovie(
                        movieId,
                        getString( R.string.language_tag ),
                        new TheMovieDb.OnFetchMovieResultListener() {
                            @Override
                            public void onFetchMovieResultListener( Movie movie ) {
                                movie.setWatched( true );
                                db.createNewMovieEntry( movie );
                            }
                        } );
                break;
            case R.id.remove_button:
                db.deleteMovieFromWatchlist( currentMovie );
                break;
            case R.id.to_watchlist_button:
                theMovieDb.fetchMovie(
                        movieId,
                        getString( R.string.language_tag ),
                        new TheMovieDb.OnFetchMovieResultListener() {
                            @Override
                            public void onFetchMovieResultListener( Movie movie ) {
                                db.createNewMovieEntry( movie );
                            }
                        } );
                break;
        }
        onBackPressed();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_movie_detail );

        Intent intent = getIntent();
        movieId = intent.getLongExtra( BaseDao.MovieEntry._ID, -1 );
        int state = intent.getIntExtra( getString( R.string.state ), -1 );

        initToolbar();

        movieTitle = (TextView) findViewById( R.id.movie_title );
        movieRuntime = (TextView) findViewById( R.id.movie_runtime );
        movieVote = (TextView) findViewById( R.id.movie_vote );
        movieDescription = (TextView) findViewById( R.id.movie_description );
        moviePoster = (ImageView) findViewById( R.id.movie_poster );
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById( R.id.swipe_refresh );

        db = MovieDbHelper.getInstance( this );
        theMovieDb = new TheMovieDb();

        initButtons( state );

        initSwipeRefresh();
        movieDbHelper = MovieDbHelper.getInstance( this );
        currentMovie = movieDbHelper.readMovie( movieId );
        if( currentMovie == null ) {
            if( NetworkChangeReceiver.getInstance().isConnected ) {
                TheMovieDb theMovieDb = new TheMovieDb();
                theMovieDb.fetchMovie( movieId, getResources().getString( R.string.language_tag ),
                        new TheMovieDb.OnFetchMovieResultListener() {
                    @Override
                    public void onFetchMovieResultListener( Movie movie ) {
                        assignData( movie );
                    }
                } );
            }
        } else {
            assignData( currentMovie );
        }
    }

    private void assignData( Movie currentMovie ) {
        Resources resources = getResources();
        movieTitle.setText( currentMovie.getTitle() );
        String description = currentMovie.getDescription();
        movieRuntime.setText( resources.getString( R.string.runtime, currentMovie.getRuntime() ) );
        movieVote.setText( resources.getString( R.string.vote, currentMovie.getVoteAverage() ) );
        movieDescription.setText(
                (description == null || description.isEmpty())
                        ? resources.getString( R.string.noDescription ) : description );

        String posterUri = Constants.POSTER_URI
                .replace( "<posterName>", currentMovie.getPosterPath() != null ?
                        currentMovie.getPosterPath() : "/" );
        Picasso.with( this )
                .load( posterUri )
                .error( R.drawable.placeholder_poster )
                .into( moviePoster );
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null )
            actionBar.setDisplayHomeAsUpEnabled( true );
    }

    private void initSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        updateMovie();
                    }
                } );
    }

    private void updateMovie() {
        if( NetworkChangeReceiver.getInstance().isConnected ) {
            TheMovieDb theMovieDb = new TheMovieDb();
            theMovieDb.fetchMovie( movieId, getResources().getString( R.string.language_tag ),
                    new TheMovieDb.OnFetchMovieResultListener() {
                @Override
                public void onFetchMovieResultListener( Movie movie ) {
                    assignData( movie );
                    updateMovieDetails( movie );
                    movieDbHelper.createOrUpdate( currentMovie );
                    swipeRefreshLayout.setRefreshing( false );
                }
            } );
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

    private void initButtons( int state ) {
        ImageButton addMovie = (ImageButton) findViewById( R.id.to_watchlist_button );
        ImageButton addMovieToWatchlist = (ImageButton) findViewById( R.id.watched_button );
        ImageButton deleteMovie = (ImageButton) findViewById( R.id.remove_button );

        addMovie.setOnClickListener( this );
        addMovieToWatchlist.setOnClickListener( this );
        deleteMovie.setOnClickListener( this );

        switch ( state ) {
            case R.string.searchState:
                addMovie.setVisibility( View.VISIBLE );
                addMovieToWatchlist.setVisibility( View.VISIBLE );
                deleteMovie.setVisibility( View.GONE );
                break;
            case R.string.watchlistState:
                addMovie.setVisibility( View.GONE );
                addMovieToWatchlist.setVisibility( View.VISIBLE );
                deleteMovie.setVisibility( View.VISIBLE );
                break;
            case R.string.watchedlistState:
                addMovie.setVisibility( View.GONE );
                addMovieToWatchlist.setVisibility( View.GONE );
                deleteMovie.setVisibility( View.VISIBLE );
                break;
        }

    }

}