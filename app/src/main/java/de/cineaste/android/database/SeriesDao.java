package de.cineaste.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.series.Season;
import de.cineaste.android.entity.series.Series;

public class SeriesDao extends BaseDao {

    private final SimpleDateFormat sdf;
    private final SeasonDao seasonDao;
    private static SeriesDao instance;

    private SeriesDao(Context context) {
        super(context);
        this.seasonDao = SeasonDao.getInstance(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd", context.getResources().getConfiguration().locale);
    }

    public static SeriesDao getInstance(Context context) {
        if (instance == null) {
            instance = new SeriesDao(context);
        }

        return instance;
    }

    void reorder(String statement) {
        writeDb.execSQL(statement);
    }

    void create(Series series) {
        ContentValues values = new ContentValues();
        values.put(SeriesEntry._ID, series.getId());
        values.put(SeriesEntry.COLUMN_SERIES_NAME, series.getName());
        values.put(SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE, series.getVoteAverage());
        values.put(SeriesEntry.COLUMN_SERIES_VOTE_COUNT, series.getVoteCount());
        values.put(SeriesEntry.COLUMN_SERIES_DESCRIPTION, series.getDescription());
        if (series.getReleaseDate() == null) {
            values.put(BaseDao.SeriesEntry.COLUMN_SERIES_RELEASE_DATE, "");
        } else {
            values.put(BaseDao.SeriesEntry.COLUMN_SERIES_RELEASE_DATE, sdf.format(series.getReleaseDate()));
        }
        values.put(SeriesEntry.COLUMN_SERIES_IN_PRODUCTION, series.isInProduction() ? 1 : 0);
        values.put(SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES, series.getNumberOfEpisodes());
        values.put(SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS, series.getNumberOfSeasons());
        values.put(SeriesEntry.COLUMN_SERIES_POSTER_PATH, series.getPosterPath());
        values.put(SeriesEntry.COLUMN_SERIES_BACKDROP_PATH, series.getBackdropPath());
        values.put(SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_EPISODE, series.getCurrentNumberOfEpisode());
        values.put(SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_SEASON, series.getCurrentNumberOfSeason());
        values.put(SeriesEntry.COLUMN_SERIES_SERIES_WATCHED, series.isWatched() ? 1 : 0);
        values.put(SeriesEntry.COLUMN_SERIES_LIST_POSITION, getHighestListPosition(series.isWatched()));

        writeDb.insert(SeriesEntry.TABLE_NAME, null, values);

        for (Season season : series.getSeasons()) {
            seasonDao.create(season, series.getId());
        }
    }

    List<Series> read(String selection, String[] selectionArgs, String orderBy) {
        List<Series> series = new ArrayList<>();

        String[] projection = {
                SeriesEntry._ID,
                SeriesEntry.COLUMN_SERIES_NAME,
                SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE,
                SeriesEntry.COLUMN_SERIES_VOTE_COUNT,
                SeriesEntry.COLUMN_SERIES_DESCRIPTION,
                SeriesEntry.COLUMN_SERIES_RELEASE_DATE,
                SeriesEntry.COLUMN_SERIES_IN_PRODUCTION,
                SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES,
                SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS,
                SeriesEntry.COLUMN_SERIES_POSTER_PATH,
                SeriesEntry.COLUMN_SERIES_BACKDROP_PATH,
                SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_EPISODE,
                SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_SEASON,
                SeriesEntry.COLUMN_SERIES_SERIES_WATCHED,
                SeriesEntry.COLUMN_SERIES_LIST_POSITION
        };

        Cursor c = readDb.query(
                SeriesEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                orderBy,
                null);

        if (c.moveToFirst()) {
            do {
                Series currentSeries = new Series();
                currentSeries.setId(
                        c.getLong(c.getColumnIndexOrThrow(SeriesEntry._ID)));
                currentSeries.setName(
                        c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_NAME)));
                currentSeries.setVoteAverage(
                        c.getDouble(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE)));
                currentSeries.setVoteCount(
                        c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_VOTE_COUNT)));
                currentSeries.setDescription(
                        c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_DESCRIPTION)));
                try {
                    currentSeries.setReleaseDate(
                            sdf.parse(c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_RELEASE_DATE))));
                } catch (Exception ex) {
                    currentSeries.setReleaseDate(null);
                }
                currentSeries.setInProduction(
                        c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_IN_PRODUCTION)) > 0);
                currentSeries.setNumberOfEpisodes(
                        c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES)));
                currentSeries.setNumberOfSeasons(
                        c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS)));
                currentSeries.setPosterPath(
                        c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_POSTER_PATH)));
                currentSeries.setBackdropPath(
                        c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_BACKDROP_PATH)));
                currentSeries.setCurrentNumberOfEpisode(
                        c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_EPISODE)));
                currentSeries.setCurrentNumberOfSeason(
                        c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_SEASON)));
                currentSeries.setWatched(
                        c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_SERIES_WATCHED)) > 0);
                currentSeries.setListPosition(
                        c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_LIST_POSITION)));

                currentSeries.setSeasons(addSeasons(currentSeries.getId()));

                series.add(currentSeries);
            } while (c.moveToNext());
        }
        c.close();
        return series;
    }

    private List<Season> addSeasons(long seriesId) {
        String selection = SeasonEntry.COLUMN_SEASON_SERIES_ID + " = ?";
        String[] selectionArgs = {Long.toString(seriesId)};

        return seasonDao.read(selection, selectionArgs);
    }

    void update(Series series, int listPosition) {
        ContentValues values = new ContentValues();
        values.put(BaseDao.SeriesEntry._ID, series.getId());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_NAME, series.getName());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE, series.getVoteAverage());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_VOTE_COUNT, series.getVoteCount());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_DESCRIPTION, series.getDescription());
        if (series.getReleaseDate() == null) {
            values.put(BaseDao.SeriesEntry.COLUMN_SERIES_RELEASE_DATE, "");
        } else {
            values.put(BaseDao.SeriesEntry.COLUMN_SERIES_RELEASE_DATE, sdf.format(series.getReleaseDate()));
        }
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_IN_PRODUCTION, series.isInProduction() ? 1 : 0);
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES, series.getNumberOfEpisodes());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS, series.getNumberOfSeasons());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_POSTER_PATH, series.getPosterPath());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_BACKDROP_PATH, series.getBackdropPath());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_EPISODE, series.getCurrentNumberOfEpisode());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_SEASON, series.getCurrentNumberOfSeason());
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_SERIES_WATCHED, series.isWatched() ? 1 : 0);
        values.put(BaseDao.SeriesEntry.COLUMN_SERIES_LIST_POSITION, listPosition);

        String selection = BaseDao.SeriesEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(series.getId())};

        for (Season season : series.getSeasons()) {
            season.setSeriesId(series.getId());
            seasonDao.update(season);
        }

        writeDb.update(SeriesEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    void delete(long id) {
        writeDb.delete(SeriesEntry.TABLE_NAME, SeriesEntry._ID + " = ?", new String[]{id + ""});
    }

    int getHighestListPosition(boolean watchState) {
        int highestPos = 0;

        String[] projection = {
                "MAX(" + SeriesEntry.COLUMN_SERIES_LIST_POSITION + ") AS POS"
        };

        String selection = SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + " = ?";

        String selectionArg;
        if (watchState) {
            selectionArg = "1";
        } else {
            selectionArg = "0";
        }

        Cursor c = writeDb.query(
                SeriesEntry.TABLE_NAME,
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
