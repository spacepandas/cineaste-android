package de.cineaste.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.Constants;
import de.cineaste.entity.Movie;


/**
 * Created by christianbraun on 17/11/15.
 */
public class MovieDao extends BaseDao {
    private static final String TAG = "MOVIEDB";
    private static MovieDao mInstance;

    public static final int DATABASE_VERSION = Constants.DATABASE_VERSION;
    public static final String DATABASE_NAME = Constants.DATABASE_NAME;


    private MovieDao( Context context ) {
        super(context);
    }

    public static MovieDao getInstance(Context context){
        if( mInstance == null){
            mInstance = new MovieDao(context);
        }
        return mInstance;
    }

    public long create(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put( MovieEntry._ID, movie.getId());
        values.put( MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        values.put( MovieEntry.COlUMN_POSTER_PATH, movie.getPosterPath());
        values.put( MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        values.put( MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        values.put( MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        values.put( MovieEntry.COLUMN_MOVIE_WATCHED, movie.isWatched() ? 1 : 0);

        long newRowId;
        newRowId = db.insert( MovieEntry.TABLE_NAME, null, values);
android.util.Log.d(TAG, "Saved movie with name " + movie.getTitle());
        db.close();
        return newRowId;
    }


    public List<Movie> read(String selection, String[] selectionArgs){
        SQLiteDatabase db = this.getReadableDatabase();
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

        Cursor c = db.query( MovieEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,null,null);

        if(c.moveToFirst()){
            do{
                Movie currentMovie = new Movie();
                currentMovie.setId(c.getLong(c.getColumnIndexOrThrow( MovieEntry._ID)));
                currentMovie.setTitle(c.getString(c.getColumnIndexOrThrow( MovieEntry.COLUMN_MOVIE_TITLE)));
                currentMovie.setPosterPath(c.getString(c.getColumnIndexOrThrow( MovieEntry.COlUMN_POSTER_PATH)));
                currentMovie.setRuntime(c.getInt(c.getColumnIndexOrThrow( MovieEntry.COLUMN_RUNTIME)));
                currentMovie.setVoteAverage(c.getDouble(c.getColumnIndexOrThrow( MovieEntry.COLUMN_VOTE_AVERAGE)));
                currentMovie.setVoteCount(c.getInt(c.getColumnIndexOrThrow( MovieEntry.COLUMN_VOTE_COUNT)));

                currentMovie.setWatched(c.getInt(c.getColumnIndexOrThrow( MovieEntry.COLUMN_MOVIE_WATCHED)) > 0);

                movies.add(currentMovie);
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return movies;
    }

    public void deleteMovieFromWatchlist(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete( MovieEntry.TABLE_NAME, MovieEntry._ID + " = ?", new String[]{id + ""});
android.util.Log.d(TAG, "Deleted movie with id = " + id);
    }

    public int update(ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update( MovieEntry.TABLE_NAME,values,selection,selectionArgs);
    }

    public void delete(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete( MovieEntry.TABLE_NAME, MovieEntry._ID +" = ?", new String[] {id + ""});
android.util.Log.d(TAG, "Deleted movie with id = " + id);
    }

    public int getRowCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT count(*) FROM " + MovieEntry.TABLE_NAME;
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int rowCount = c.getInt(0);
        c.close();
        db.close();
        return rowCount;
    }
}
