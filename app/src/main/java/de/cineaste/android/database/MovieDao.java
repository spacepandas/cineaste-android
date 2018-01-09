package de.cineaste.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.movie.Movie;

class MovieDao extends BaseDao {
    private final SimpleDateFormat sdf;
    private static MovieDao mInstance;

    private MovieDao(Context context) {
        super(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd", context.getResources().getConfiguration().locale);
    }

    public static MovieDao getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MovieDao(context);
        }

        return mInstance;
    }

    void reorder(String statement) {
        writeDb.execSQL(statement);
    }

    void create(Movie movie) {
        ContentValues values = new ContentValues();
        values.put(MovieEntry._ID, movie.getId());
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        values.put(MovieEntry.COlUMN_POSTER_PATH, movie.getPosterPath());
        values.put(MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        values.put(MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        values.put(MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        values.put(MovieEntry.COLUMN_MOVIE_DESCRIPTION, movie.getDescription());
        values.put(MovieEntry.COLUMN_MOVIE_WATCHED, movie.isWatched() ? 1 : 0);
        values.put(MovieEntry.COLUMN_MOVIE_WATCHED_DATE, movie.getWatchedDate());
        if (movie.getReleaseDate() != null) {
            values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, sdf.format(movie.getReleaseDate()));
        } else {
            values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "");
        }

        values.put(MovieEntry.COLUMN_MOVIE_LIST_POSITION, getHighestListPosition(movie.isWatched()) + 1);

        writeDb.insert(MovieEntry.TABLE_NAME, null, values);
    }

    List<Movie> read(String selection, String[] selectionArgs, String orderBy) {
        List<Movie> movies = new ArrayList<>();

        String[] projection = {
                MovieEntry._ID,
                MovieEntry.COLUMN_MOVIE_TITLE,
                MovieEntry.COlUMN_POSTER_PATH,
                MovieEntry.COLUMN_RUNTIME,
                MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieEntry.COLUMN_VOTE_COUNT,
                MovieEntry.COLUMN_MOVIE_DESCRIPTION,
                MovieEntry.COLUMN_MOVIE_WATCHED,
                MovieEntry.COLUMN_MOVIE_WATCHED_DATE,
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                MovieEntry.COLUMN_MOVIE_LIST_POSITION
        };

        Cursor c = readDb.query(
                MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderBy,
                null);

        if (c.moveToFirst()) {
            do {
                Movie currentMovie = new Movie();
                currentMovie.setId(
                        c.getLong(c.getColumnIndexOrThrow(MovieEntry._ID)));
                currentMovie.setTitle(
                        c.getString(c.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_TITLE)));
                currentMovie.setPosterPath(
                        c.getString(c.getColumnIndexOrThrow(MovieEntry.COlUMN_POSTER_PATH)));
                currentMovie.setRuntime(
                        c.getInt(c.getColumnIndexOrThrow(MovieEntry.COLUMN_RUNTIME)));
                currentMovie.setVoteAverage(
                        c.getDouble(c.getColumnIndexOrThrow(MovieEntry.COLUMN_VOTE_AVERAGE)));
                currentMovie.setVoteCount(
                        c.getInt(c.getColumnIndexOrThrow(MovieEntry.COLUMN_VOTE_COUNT)));
                currentMovie.setDescription(
                        c.getString(c.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_DESCRIPTION)));
                currentMovie.setWatched(
                        c.getInt(c.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_WATCHED)) > 0);
                currentMovie.setWatchedDate(
                        c.getLong(c.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_WATCHED_DATE)));
                try {
                    currentMovie.setReleaseDate(
                            sdf.parse(c.getString(c.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_RELEASE_DATE)))
                    );
                } catch (Exception ex) {
                    currentMovie.setReleaseDate(null);
                }

                currentMovie.setListPosition(c.getInt(c.getColumnIndexOrThrow(MovieEntry.COLUMN_MOVIE_LIST_POSITION)));

                movies.add(currentMovie);
            } while (c.moveToNext());
        }
        c.close();
        return movies;
    }

    void update(ContentValues values, String selection, String[] selectionArgs) {
        writeDb.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    void delete(long id) {
        writeDb.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + " = ?", new String[]{id + ""});
    }

    int getHighestListPosition(boolean watchState) {
        int highestPos = 0;

        String[] projection = {
                "MAX(" + MovieEntry.COLUMN_MOVIE_LIST_POSITION + ") AS POS"
        };

        String selection = MovieEntry.COLUMN_MOVIE_WATCHED + " = ?";

        String selectionArg;
        if (watchState) {
            selectionArg = "1";
        } else {
            selectionArg = "0";
        }

        Cursor c = writeDb.query(
                MovieEntry.TABLE_NAME,
                projection,
                selection,
                new String[]{selectionArg},
                null,
                null,
                null);
        if (c.moveToFirst()) {
            do {
                highestPos = c.getInt(c.getColumnIndex("POS"));
            } while (c.moveToNext());
        }

        c.close();
        return highestPos;
    }
}
