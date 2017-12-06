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

    private MovieDbHelper(Context context) {
        this.movieDao = MovieDao.getInstance(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd", context.getResources().getConfiguration().locale);
    }

    public static MovieDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MovieDbHelper(context);
        }
        return instance;
    }

    public Movie readMovie(long movieId) {
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(movieId)};

        List<Movie> movies = movieDao.read(selection, selectionArgs);

        return movies.size() == 0 ? null : movies.get(0);
    }

    public List<Movie> readAllMovies() {
        return movieDao.read(null, null);
    }

    public List<Movie> readMoviesByWatchStatus(WatchState state) {
        String selectionArg;
        if (state == WatchState.WATCH_STATE) {
            selectionArg = "0";
        } else {
            selectionArg = "1";
        }
        String selection = BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = ?";
        String[] selectionArgs = {selectionArg};

        return movieDao.read(selection, selectionArgs);
    }

    public void createOrUpdate(Movie movie) {
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(movie.getId())};
        List<Movie> movieList = movieDao.read(selection, selectionArgs);

        if (!movieList.isEmpty()) {
            update(movie, movieList.get(0));
        } else {
            movieDao.create(movie);
        }

    }

    public void deleteMovieFromWatchlist(Movie movie) {
        movieDao.delete(movie.getId());
    }

    private void update(Movie updatedMovie, Movie oldMovie) {
        ContentValues values = new ContentValues();
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED, updatedMovie.isWatched() ? 1 : 0);
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED_DATE, updatedMovie.getWatchedDate());
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_TITLE, updatedMovie.getTitle());
        values.put(BaseDao.MovieEntry.COLUMN_RUNTIME, updatedMovie.getRuntime());
        values.put(BaseDao.MovieEntry.COLUMN_VOTE_AVERAGE, updatedMovie.getVoteAverage());
        values.put(BaseDao.MovieEntry.COLUMN_VOTE_COUNT, updatedMovie.getVoteCount());
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_DESCRIPTION, updatedMovie.getDescription());
        values.put(BaseDao.MovieEntry.COlUMN_POSTER_PATH, updatedMovie.getPosterPath());
        if (updatedMovie.getReleaseDate() != null) {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, sdf.format(updatedMovie.getReleaseDate()));
        } else {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "");
        }
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION, getNewPosition(updatedMovie, oldMovie));
        String selection = BaseDao.MovieEntry._ID + " LIKE ?";
        String[] where = {String.valueOf(updatedMovie.getId())};

        movieDao.update(values, selection, where);
    }

    private int getNewPosition(Movie updatedMovie, Movie oldMovie) {
        if (updatedMovie.isWatched() == oldMovie.isWatched()) {
            return oldMovie.getListPosition();
        }

        return movieDao.getHighestListPosition(updatedMovie.isWatched());
    }

}
