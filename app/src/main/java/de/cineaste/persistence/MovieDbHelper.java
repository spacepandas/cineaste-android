package de.cineaste.persistence;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;
import java.util.Observable;

import de.cineaste.entity.Movie;

/**
 * Created by christianbraun on 17/11/15.
 */
public class MovieDbHelper extends Observable {

    private static MovieDbHelper mInstance;

    private Context context;
    private MovieDao mMovieDao;

    private MovieDbHelper( Context context ) {
        this.context = context;
        this.mMovieDao = MovieDao.getInstance( context );
    }

    public static MovieDbHelper getInstance(Context context) {
        if ( mInstance == null) {
            mInstance = new MovieDbHelper(context);
        }
        return mInstance;
    }

    public long createNewMovieEntry(Movie movie) {
        return mMovieDao.create(movie);
    }

    public List<Movie> readAllMovies() {
        return mMovieDao.read(null, null);
    }

    public List<Movie> readMoviesByWatchStatus(Boolean watched) {
        String selection = BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = ?";
        String[] selectionArgs = {Integer.toString(watched ? 1 : 0)};

        return mMovieDao.read(selection, selectionArgs);
    }

    public void createOrUpdate(Movie movie){
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString( movie.getId() )};
        List<Movie> movieList =  mMovieDao.read(selection, selectionArgs);

        if(!movieList.isEmpty()){
            updateMovieWatched(movie.isWatched(), movie.getId());
        }
        else{
            createNewMovieEntry(movie);
        }

    }

    public void deleteMovieFromWatchlist(long movieId) {
        mMovieDao.delete( movieId );
    }

    public int getMovieCount() {
        return mMovieDao.getRowCount();
    }

    public int updateMovieWatched(Boolean watched, Long dbId) {
        ContentValues values = new ContentValues();
        values.put( BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED, watched ? 1 : 0);
        String selection = BaseDao.MovieEntry._ID + " LIKE ?";
        String[] where = {String.valueOf(dbId)};

        int affectedRows = mMovieDao.update(values, selection, where);
        if (affectedRows > 0) {
            setChanged();
            notifyObservers();
        }

        return affectedRows;
    }
}
