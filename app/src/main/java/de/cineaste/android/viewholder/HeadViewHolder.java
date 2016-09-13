package de.cineaste.android.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.TheMovieDb;


public class HeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final TextView movieTitle;
    private final TextView movieRuntime;
    private final ImageButton addToWatchList;
    private final ImageButton addToWatchedList;
    private final ImageButton delete;
    private Movie currentMovie;
    private final Context context;
    private final OnBackPressedListener listener;
    private final int state;

    public interface OnBackPressedListener {
        void onBackPressedListener();
    }

    public HeadViewHolder( View v, Context context, int state, OnBackPressedListener listener ) {
        super( v );
        this.state = state;
        this.context = context;
        movieTitle = (TextView) v.findViewById( R.id.movieTitle );
        movieRuntime = (TextView) v.findViewById( R.id.movieRuntime );
        addToWatchList = (ImageButton) v.findViewById( R.id.addToWatchList );
        addToWatchedList = (ImageButton) v.findViewById( R.id.addToWatchedList );
        delete = (ImageButton) v.findViewById( R.id.remove );
        this.listener = listener;
        initButtons();
    }

    public void assignData( final Movie movie ) {
        Resources resources = context.getResources();
        currentMovie = movie;
        movieTitle.setText( movie.getTitle() );
        movieRuntime.setText( resources.getString(R.string.runtime, movie.getRuntime()) );
    }

    @Override
    public void onClick( View v ) {
        final MovieDbHelper db = MovieDbHelper.getInstance( context );
        TheMovieDb theMovieDb = new TheMovieDb();

        switch ( v.getId() ) {
            case R.id.addToWatchedList:
                theMovieDb.fetchMovie(
                        currentMovie.getId(),
                        context.getString( R.string.language_tag ),
                        new TheMovieDb.OnFetchMovieResultListener() {
                            @Override
                            public void onFetchMovieResultListener( Movie movie ) {
                                movie.setWatched( true );

                                if (state == R.string.searchState ) {
                                    db.createOrUpdate( movie );
                                } else {
                                    db.update(movie);
                                }
                            }
                        } );
                break;
            case R.id.remove:
                db.deleteMovieFromWatchlist( currentMovie );
                break;
            case R.id.addToWatchList:
                theMovieDb.fetchMovie(
                        currentMovie.getId(),
                        context.getString( R.string.language_tag ),
                        new TheMovieDb.OnFetchMovieResultListener() {
                            @Override
                            public void onFetchMovieResultListener( Movie movie ) {
                                db.createNewMovieEntry( movie );
                            }
                        } );
                break;
        }
        listener.onBackPressedListener();
    }

    private void initButtons() {

        addToWatchedList.setOnClickListener( this );
        addToWatchList.setOnClickListener( this );
        delete.setOnClickListener( this );

        switch ( state ) {
            case R.string.searchState:
                addToWatchedList.setVisibility( View.VISIBLE );
                addToWatchList.setVisibility( View.VISIBLE );
                delete.setVisibility( View.GONE );
                break;
            case R.string.watchlistState:
                addToWatchedList.setVisibility( View.VISIBLE );
                addToWatchList.setVisibility( View.GONE );
                delete.setVisibility( View.VISIBLE );
                break;
            case R.string.watchedlistState:
                addToWatchedList.setVisibility( View.GONE );
                addToWatchList.setVisibility( View.GONE );
                delete.setVisibility( View.VISIBLE );
                break;
        }

    }
}