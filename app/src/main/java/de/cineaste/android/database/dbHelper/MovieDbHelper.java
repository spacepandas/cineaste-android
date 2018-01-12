package de.cineaste.android.database.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;

import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dao.MovieDao;
import de.cineaste.android.entity.movie.Movie;
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

        List<Movie> movies = movieDao.read(selection, selectionArgs, null);

        return movies.size() == 0 ? null : movies.get(0);
    }

    public List<Movie> readAllMovies() {
        return movieDao.read(null, null, null);
    }

    public List<Movie> readMoviesByWatchStatus(WatchState state) {
        String selectionArg = getSelectionArgs(state);
        String selection = BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = ?";
        String[] selectionArgs = {selectionArg};

        return movieDao.read(selection, selectionArgs, BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION + " ASC");
    }

    public List<Movie> reorderAlphabetical(WatchState state) {
        return reorder(state, BaseDao.MovieEntry.COLUMN_MOVIE_TITLE);
    }

    public List<Movie> reorderByReleaseDate(WatchState state) {
        return reorder(state, BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
    }

    public List<Movie> reorderByRuntime(WatchState state) {
        return reorder(state, BaseDao.MovieEntry.COLUMN_RUNTIME);
    }

    private List<Movie> reorder(WatchState state, String orderBy) {
        String sql = "UPDATE " + BaseDao.MovieEntry.TABLE_NAME +
                " SET " + BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION + " = ( " +
                    "SELECT COUNT(*) " +
                    "FROM " + BaseDao.MovieEntry.TABLE_NAME + " AS t2 " +
                    "WHERE t2." + orderBy + " <= " + BaseDao.MovieEntry.TABLE_NAME + "." + orderBy +
                    " ) " +
                "WHERE " + BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED + " = " + getSelectionArgs(state);
        movieDao.reorder(sql);

        return readMoviesByWatchStatus(state);
    }

    @NonNull
    private String getSelectionArgs(WatchState state) {
        String selectionArg;
        if (state == WatchState.WATCH_STATE) {
            selectionArg = "0";
        } else {
            selectionArg = "1";
        }
        return selectionArg;
    }

    public void createOrUpdate(Movie movie) {
        String selection = BaseDao.MovieEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(movie.getId())};
        List<Movie> movieList = movieDao.read(selection, selectionArgs, null);

        if (!movieList.isEmpty()) {
            update(movie, getNewPosition(movie, movieList.get(0)));
        } else {
            movieDao.create(movie);
        }
    }

    public void updatePosition(Movie movie) {
        update(movie, movie.getListPosition());
    }

    public void deleteMovieFromWatchlist(Movie movie) {
        movieDao.delete(movie.getId());
    }

    private void update(Movie movie, int listPosition) {
        ContentValues values = new ContentValues();
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED, movie.isWatched() ? 1 : 0);
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_WATCHED_DATE, movie.getWatchedDate());
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        values.put(BaseDao.MovieEntry.COLUMN_RUNTIME, movie.getRuntime());
        values.put(BaseDao.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        values.put(BaseDao.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_DESCRIPTION, movie.getDescription());
        values.put(BaseDao.MovieEntry.COlUMN_POSTER_PATH, movie.getPosterPath());
        if (movie.getReleaseDate() != null) {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, sdf.format(movie.getReleaseDate()));
        } else {
            values.put(BaseDao.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "");
        }
        values.put(BaseDao.MovieEntry.COLUMN_MOVIE_LIST_POSITION, listPosition);
        String selection = BaseDao.MovieEntry._ID + " LIKE ?";
        String[] where = {String.valueOf(movie.getId())};

        movieDao.update(values, selection, where);
    }

    private int getNewPosition(Movie updatedMovie, Movie oldMovie) {
        if (updatedMovie.isWatched() == oldMovie.isWatched()) {
            return oldMovie.getListPosition();
        }

        return movieDao.getHighestListPosition(updatedMovie.isWatched());
    }

}
