package de.cineaste.android.database;

import android.content.ContentValues;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.List;

import de.cineaste.android.entity.Movie;
import de.cineaste.android.fragment.WatchState;

public class MovieDbHelper {

    private static MovieDbHelper instance;

    private final MovieDao movieDao;
    private final SimpleDateFormat sdf;

    private MovieDbHelper( Context context ) {
        this.movieDao = MovieDao.getInstance( context );
        sdf = new SimpleDateFormat("yyyy-MM-dd", context.getResources().getConfiguration().locale);
    }

    public static MovieDbHelper getInstance( Context context ) {
        if( instance == null ) {
            instance = new MovieDbHelper( context );
        }
        return instance;
    }

    public long createNewMovieEntry( Movie movie ) {
        return movieDao.create( movie );
    }

    public Movie readMovie( long movieId ) {
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString( movieId )};

        List<Movie> movies = movieDao.read( selection, selectionArgs );

        return movies.size() == 0 ? null : movies.get( 0 );
    }

    public List<Movie> readAllMovies() {
        return movieDao.read( null, null );
    }

    @Deprecated
    public List<Movie> readMoviesByWatchStatus( Boolean watched ) {
        String selection = BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = ?";
        String[] selectionArgs = {Integer.toString( watched ? 1 : 0 )};

        return movieDao.read( selection, selectionArgs );
    }

    public List<Movie> readMoviesByWatchStatus( WatchState state ) {
        String selectionArg;
        if (state == WatchState.WATCH_STATE) {
            selectionArg = "0";
        } else {
            selectionArg = "1";
        }
        String selection = BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = ?";
        String[] selectionArgs = {selectionArg};

        return movieDao.read( selection, selectionArgs );
    }

    public void createOrUpdate( Movie movie ) {
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString( movie.getId() )};
        List<Movie> movieList = movieDao.read( selection, selectionArgs );

        if( !movieList.isEmpty() ) {
            updateMovieWatched( movie );
        } else {
            createNewMovieEntry( movie );
        }

    }

    public void justUpdate(Movie movie) {
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString( movie.getId() )};
        List<Movie> movieList = movieDao.read( selection, selectionArgs );

        if( !movieList.isEmpty() ) {
            updateMovieWatched( movie );
        }

    }

    public void update(Movie movie) {
        updateMovieWatched(movie);
    }

    public void deleteMovieFromWatchlist( Movie movie ) {
        movieDao.delete(movie.getId());
    }

    private int updateMovieWatched( Movie movie ) {
        ContentValues values = new ContentValues();
        values.put( BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED, movie.isWatched() ? 1 : 0 );
        values.put( BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED_DATE, movie.getWatchedDate() );
        values.put( BaseDao.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle() );
        values.put( BaseDao.MovieEntry.COLUMN_RUNTIME, movie.getRuntime() );
        values.put( BaseDao.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage() );
        values.put( BaseDao.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount() );
        values.put( BaseDao.MovieEntry.COLUMN_MOVIE_DESCRIPTION, movie.getDescription() );
        values.put( BaseDao.MovieEntry.COlUMN_POSTER_PATH, movie.getPosterPath() );
        if (movie.getReleaseDate() != null) {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, sdf.format(movie.getReleaseDate()));
        } else {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "");
        }
        String selection = BaseDao.MovieEntry._ID + " LIKE ?";
        String[] where = {String.valueOf( movie.getId() )};

        return  movieDao.update( values, selection, where );
    }

}
