package de.cineaste.android.database;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;
import java.util.Observable;

import de.cineaste.android.entity.Movie;

public class MovieDbHelper extends Observable {

    private static MovieDbHelper instance;

    private final MovieDao movieDao;

    private MovieDbHelper( Context context ) {
        this.movieDao = MovieDao.getInstance( context );
    }

    public static MovieDbHelper getInstance(Context context) {
        if ( instance == null) {
            instance = new MovieDbHelper(context);
        }
        return instance;
    }

    public long createNewMovieEntry(Movie movie) {
        return movieDao.create( movie );
    }

    public Movie readMovie(long movieId) {
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString( movieId )};

        return movieDao.read( selection, selectionArgs ).get( 0 );
    }

    public List<Movie> readAllMovies() {
        return movieDao.read( null, null );
    }

    public List<Movie> readMoviesByWatchStatus(Boolean watched) {
        String selection = BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = ?";
        String[] selectionArgs = {Integer.toString(watched ? 1 : 0)};

        return movieDao.read(selection, selectionArgs);
    }

    public void createOrUpdate(Movie movie){
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString( movie.getId() )};
        List<Movie> movieList =  movieDao.read(selection, selectionArgs);

        if(!movieList.isEmpty()){
            updateMovieWatched(movie.isWatched(), movie.getId());
        }
        else{
            createNewMovieEntry(movie);
        }
    }

    public void deleteMovieFromWatchlist(long movieId) {
        movieDao.delete( movieId );
    }

    public int getMovieCount() {
        return movieDao.getRowCount();
    }

    public int updateMovieWatched(Boolean watched, Long dbId) {
        ContentValues values = new ContentValues();
        values.put( BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED, watched ? 1 : 0);
        String selection = BaseDao.MovieEntry._ID + " LIKE ?";
        String[] where = {String.valueOf(dbId)};

        int affectedRows = movieDao.update(values, selection, where);
        if (affectedRows > 0) {
            setChanged();
            notifyObservers();
        }

        return affectedRows;
    }
}
