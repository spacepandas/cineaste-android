package de.cineaste.android.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.Movie;

public class MovieDao extends BaseDao {
    private static final String TAG = "MOVIEDB";
    private static MovieDao mInstance;

    private MovieDao( Context context ) {
        super( context );
    }

    public static MovieDao getInstance( Context context ) {
        if( mInstance == null ) {
            mInstance = new MovieDao( context );
        }

        return mInstance;
    }

    public long create( Movie movie ) {
        ContentValues values = new ContentValues();

        values.put( MovieEntry._ID, movie.getId() );
        values.put( MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle() );
        values.put( MovieEntry.COlUMN_POSTER_PATH, movie.getPosterPath() );
        values.put( MovieEntry.COLUMN_RUNTIME, movie.getRuntime() );
        values.put( MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage() );
        values.put( MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount() );
        values.put( MovieEntry.COLUMN_MOVIE_WATCHED, movie.isWatched() ? 1 : 0 );

        long newRowId;
        newRowId = writeDb.insert( MovieEntry.TABLE_NAME, null, values );
android.util.Log.d( TAG, "Saved movie with name " + movie.getTitle() );

        return newRowId;
    }

    public List<Movie> read( String selection, String[] selectionArgs ) {
        List<Movie> movies = new ArrayList<>();

        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_MOVIE_TITLE,
                MovieEntry.COlUMN_POSTER_PATH,
                MovieEntry.COLUMN_RUNTIME,
                MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieEntry.COLUMN_VOTE_COUNT,
                MovieEntry.COLUMN_MOVIE_WATCHED
        };

        Cursor c = readDb.query(
                MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
                null );

        if( c.moveToFirst() ) {
            do {
                Movie currentMovie = new Movie();
                currentMovie.setId(
                        c.getLong( c.getColumnIndexOrThrow( MovieEntry._ID ) ) );
                currentMovie.setTitle(
                        c.getString( c.getColumnIndexOrThrow( MovieEntry.COLUMN_MOVIE_TITLE ) ) );
                currentMovie.setPosterPath(
                        c.getString( c.getColumnIndexOrThrow( MovieEntry.COlUMN_POSTER_PATH ) ) );
                currentMovie.setRuntime(
                        c.getInt( c.getColumnIndexOrThrow( MovieEntry.COLUMN_RUNTIME ) ) );
                currentMovie.setVoteAverage(
                        c.getDouble( c.getColumnIndexOrThrow( MovieEntry.COLUMN_VOTE_AVERAGE ) ) );
                currentMovie.setVoteCount(
                        c.getInt( c.getColumnIndexOrThrow( MovieEntry.COLUMN_VOTE_COUNT ) ) );
                currentMovie.setWatched(
                        c.getInt( c.getColumnIndexOrThrow( MovieEntry.COLUMN_MOVIE_WATCHED ) ) > 0 );
                movies.add( currentMovie );
            } while ( c.moveToNext() );
        }
        c.close();
        return movies;
    }

    public int update( ContentValues values, String selection, String[] selectionArgs ) {
        return writeDb.update( MovieEntry.TABLE_NAME, values, selection, selectionArgs );
    }

    public void delete( long id ) {

        writeDb.delete( MovieEntry.TABLE_NAME, MovieEntry._ID + " = ?", new String[]{id + ""} );
        android.util.Log.d( TAG, "Deleted movie with id = " + id );
    }

    public int getRowCount() {
        String query = "SELECT count(*) FROM " + MovieEntry.TABLE_NAME;
        Cursor c = readDb.rawQuery( query, null );
        c.moveToFirst();
        int rowCount = c.getInt( 0 );
        c.close();
        return rowCount;
    }
}
