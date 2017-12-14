package de.cineaste.android.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.List;

import de.cineaste.android.entity.Season;
import de.cineaste.android.entity.Series;
import de.cineaste.android.fragment.WatchState;


public class SeriesDbHelper {

    private static SeriesDbHelper instance;

    private final SeriesDao seriesDao;
    private final SimpleDateFormat sdf;

    private SeriesDbHelper(Context context) {
        this.seriesDao = SeriesDao.getInstance(context);
        sdf = new SimpleDateFormat("yyyy-MM-dd", context.getResources().getConfiguration().locale);
    }

    public static SeriesDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SeriesDbHelper(context);
        }

        return instance;
    }

    public Series readSeries(long seriesId) {
        String selection = BaseDao.SeriesEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(seriesId)};

        List<Series> series = seriesDao.read(selection, selectionArgs, null);

        return series.size() == 0 ? null : series.get(0);
    }

    public List<Series> readAllSeries() {
        return seriesDao.read(null, null, null);
    }

    public List<Series> readSeriesByWatchStatus(WatchState state) {
        String selectionArg = getSelectionArgs(state);
        String selection = BaseDao.SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + " = ?";
        String[] selectionArgs = {selectionArg};

        return seriesDao.read(selection, selectionArgs, BaseDao.SeriesEntry.COLUMN_SERIES_LIST_POSITION + " ASC");
    }

    public void episodeWatched(Series series) {
        series = readSeries(series.getId());
        Season currentSeason = getCurrentSeason(series.getCurrentNumberOfSeason(), series);
        int updatedEpisodeIndex = series.getCurrentNumberOfEpisode() + 1;
        if (currentSeason == null) {
            return;
        }
        if (updatedEpisodeIndex > currentSeason.getEpisodeCount()) {
            int updatedSeasonIndex = series.getCurrentNumberOfSeason() + 1;
            Season newSeason = getCurrentSeason(updatedSeasonIndex, series);

            if (newSeason != null) {
                series.setCurrentNumberOfEpisode(1);
                series.setCurrentNumberOfSeason(updatedSeasonIndex);
                series.setCurrentPosterPath(newSeason.getPosterPath());
            } else {
                if (!series.isInProduction()) {
                    series.setWatched(true);
                }
            }

        } else {
            series.setCurrentNumberOfEpisode(updatedEpisodeIndex);
        }

        createOrUpdate(series);
    }

    private Season getCurrentSeason(int currentSeasonIndex, Series series) {
        for (Season season : series.getSeasons()) {
            if (season.getSeasonNumber() == currentSeasonIndex) {
                return season;
            }
        }

        return null;
    }

    public List<Series> reorderAlphabetical(WatchState state) {
        return reorder(state, BaseDao.SeriesEntry.COLUMN_SERIES_NAME);
    }


    public List<Series> reorderByReleaseDate(WatchState state) {
        return reorder(state, BaseDao.SeriesEntry.COLUMN_SERIES_RELEASE_DATE);
    }

    private List<Series> reorder(WatchState state, String orderBy) {
        String sql = "UPDATE " + BaseDao.SeriesEntry.TABLE_NAME +
                " SET " + BaseDao.SeriesEntry.COLUMN_SERIES_LIST_POSITION + " = ( " +
                "SELECT COUNT(*) " +
                "FROM " + BaseDao.SeriesEntry.TABLE_NAME + " AS t2 " +
                "WHERE t2." + orderBy + " <= " + BaseDao.SeriesEntry.TABLE_NAME + "." + orderBy +
                " ) " +
                "WHERE " + BaseDao.SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + " = " + getSelectionArgs(state);
        seriesDao.reorder(sql);

        return readSeriesByWatchStatus(state);
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

    public void createOrUpdate(Series series) {
        String selection = BaseDao.SeriesEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(series.getId())};

        List<Series> seriesList = seriesDao.read(selection, selectionArgs, null);

        if (!seriesList.isEmpty()) {
            update(series, getNewPosition(series, seriesList.get(0)));
        } else {
            series = initSeries(series);
            seriesDao.create(series);
        }
    }

    private Series initSeries(Series series) {
        if (TextUtils.isEmpty(series.getCurrentPosterPath())) {
            series.setCurrentPosterPath(series.getPosterPath());
        }
        if (series.getCurrentNumberOfSeason() == 0) {
            series.setCurrentNumberOfSeason(1);
        }
        if (series.getCurrentNumberOfEpisode() == 0) {
            series.setCurrentNumberOfEpisode(1);
        }

        return series;
    }

    public void updatePosition(Series series) {
        update(series, series.getListPosition());
    }

    private void update(Series series, int listPosition) {
        seriesDao.update(series, listPosition);
    }

    private int getNewPosition(Series updatedSeries, Series oldSeries) {
        if (updatedSeries.isWatched() == oldSeries.isWatched()) {
            return oldSeries.getListPosition();
        }

        return seriesDao.getHighestListPosition(updatedSeries.isWatched());
    }
}
