package de.cineaste.android.database.dbHelper;


import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.database.dao.BaseDao;
import de.cineaste.android.database.dao.EpisodeDao;
import de.cineaste.android.database.dao.SeasonDao;
import de.cineaste.android.database.dao.SeriesDao;
import de.cineaste.android.entity.series.Episode;
import de.cineaste.android.entity.series.Season;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.fragment.WatchState;

import static de.cineaste.android.database.dao.BaseDao.EpisodeEntry;
import static de.cineaste.android.database.dao.BaseDao.SeasonEntry;
import static de.cineaste.android.database.dao.BaseDao.SeriesEntry;

public class SeriesDbHelper {
    
    private static SeriesDbHelper instance;

    private final SeriesDao seriesDao;
    private final SeasonDao seasonDao;
    private final EpisodeDao episodeDao;

    private SeriesDbHelper(Context context) {
        this.seriesDao = SeriesDao.getInstance(context);
        this.seasonDao = SeasonDao.getInstance(context);
        this.episodeDao = EpisodeDao.getInstance(context);
    }

    public static SeriesDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SeriesDbHelper(context);
        }
        return instance;
    }
    
    public Series getSeriesById(long seriesId) {
        String selection = SeriesEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(seriesId)};

        List<Series> seriesList = seriesDao.read(selection, selectionArgs, null);

        if (seriesList.size() == 0) {
            return null;
        }

        Series series = seriesList.get(0);
        loadRemainingInformation(series);

        return series;
    }

    public List<Series> getAllSeries() {
        List<Series> seriesList = seriesDao.read(null, null, null);

        for (Series series : seriesList) {
            loadRemainingInformation(series);
        }

        return seriesList;
    }

    public List<Series> getSeriesByWatchedState(WatchState watchedState) {
        String selectionArg = getSelectionArgs(watchedState);
        String selection = SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + " = ?";
        String[] selectionArgs = {selectionArg};

        List<Series> seriesList = seriesDao.read(selection, selectionArgs, SeriesEntry.COLUMN_SERIES_LIST_POSITION + " ASC");

        for (Series series : seriesList) {
            loadRemainingInformation(series);
        }

        return seriesList;
    }

    public List<Episode> getEpisodesBySeasonId(long seasonId) {
        String selection = BaseDao.EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = ?";
        String[] selectionArgs = {String.valueOf(seasonId)};

        return episodeDao.read(selection, selectionArgs);
    }

    public List<Series> reorderAlphabetical(WatchState state) {
        return reorder(state, SeriesEntry.COLUMN_SERIES_NAME);
    }

    public List<Series> reorderByReleaseDate(WatchState state) {
        return reorder(state, SeriesEntry.COLUMN_SERIES_RELEASE_DATE);
    }

    private List<Series> reorder(WatchState state, String orderBy) {
        String sql = "UPDATE " + SeriesEntry.TABLE_NAME +
                " SET " + SeriesEntry.COLUMN_SERIES_LIST_POSITION + " = ( " +
                "SELECT COUNT(*) " +
                "FROM " + SeriesEntry.TABLE_NAME + " AS t2 " +
                "WHERE t2." + orderBy + " <= " + SeriesEntry.TABLE_NAME + "." + orderBy +
                " ) " +
                "WHERE " + SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + " = " + getSelectionArgs(state);
        seriesDao.reorder(sql);

        return getSeriesByWatchedState(state);
    }
    
    private void loadRemainingInformation(Series series) {
        List<Season> seasons = readSeasonsBySeriesId(series.getId());

        for (Season season : seasons) {
            season.setEpisodes(readEpisodesBySeasonId(season.getId()));
        }
        
        series.setSeasons(seasons);
    }
    
    private List<Season> readSeasonsBySeriesId(long seriesId) {
        String selection = SeasonEntry.COLUMN_SEASON_SERIES_ID + " = ?";
        String[] selectionArgs = {Long.toString(seriesId)};
        
        return seasonDao.read(selection, selectionArgs);
    }
    
    private List<Episode> readEpisodesBySeasonId(long seasonId) {
        String selection = EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = ?";
        String[] selectionArgs = {String.valueOf(seasonId)};

        return episodeDao.read(selection, selectionArgs);
    }
    
    public void addToWatchList(Series series) {
        addToList(series, false);
    }

    public void addToHistory(Series series) {
        addToList(series, true);
    }

    private void addToList(Series series, boolean watchState) {
        series.setWatched(watchState);
        series.setListPosition(seriesDao.getHighestListPosition(watchState));
        seriesDao.create(series);
        for (Season season : series.getSeasons()) {
            season.setWatched(watchState);
            seasonDao.create(season, series.getId());
            for (Episode episode : season.getEpisodes()) {
                episode.setWatched(watchState);
                episodeDao.create(episode, series.getId(), season.getId());
            }
        }
    }

    public void moveToWatchList(Series series) {
        moveBetweenLists(series, false);
    }

    public void moveToHistory(Series series) {
        moveBetweenLists(series, true);
    }

    private void moveBetweenLists(Series series, boolean watchState) {
        String updateEpisodesSql = "UPDATE " + EpisodeEntry.TABLE_NAME +
                " SET " + EpisodeEntry.COLUMN_EPISODE_WATCHED + " = " + String.valueOf(watchState ? 1 : 0) +
                " WHERE " + EpisodeEntry.COLUMN_EPISODE_SERIES_ID + " = " + series.getId();

        String updateSeasonsSql = "UPDATE " + SeasonEntry.TABLE_NAME +
                " SET " + SeasonEntry.COLUMN_SEASON_WATCHED + " = " + String.valueOf(watchState ? 1 : 0) +
                " WHERE " + SeasonEntry.COLUMN_SEASON_SERIES_ID + " = " + series.getId();

        series.setWatched(watchState);

        seriesDao.update(series, seriesDao.getHighestListPosition(watchState));
        seasonDao.executeCustomSql(updateSeasonsSql);
        episodeDao.executeCustomSql(updateEpisodesSql);
    }

    public void moveBackToWatchList(Series series, int prevSeason, int prevEpisode) {
        moveBackToList(series, prevSeason, prevEpisode, false);
    }

    public void moveBackToHistory(Series series, int prevSeason, int prevEpisode) {
        moveBackToList(series, prevSeason, prevEpisode, true);
    }

    private void moveBackToList(Series series, int prevSeason, int prevEpisode, boolean watchState) {
        moveBetweenLists(series, watchState);
        for (Season season : series.getSeasons()) {
            if (season.getSeasonNumber() < prevSeason) {
                season.setWatched(!watchState);
                seasonDao.update(season);

                String updateEpisodesSql = "UPDATE " + EpisodeEntry.TABLE_NAME +
                        " SET " + EpisodeEntry.COLUMN_EPISODE_WATCHED + " = " + String.valueOf(!watchState ? 1 : 0) +
                        " WHERE " + EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = " + season.getId();

                episodeDao.executeCustomSql(updateEpisodesSql);
            }

            if (season.getSeasonNumber() == prevSeason) {
                for (Episode episode : season.getEpisodes()) {
                    if (episode.getEpisodeNumber() < prevEpisode) {
                        episode.setWatched(!watchState);
                        episodeDao.update(episode);
                    }
                }
            }
        }
    }

    public void delete(Series series) {
        episodeDao.deleteBySeriesId(series.getId());
        seasonDao.deleteBySeriesId(series.getId());
        seriesDao.delete(series.getId());
    }

    public void delete(long seriesId) {
        episodeDao.deleteBySeriesId(seriesId);
        seasonDao.deleteBySeriesId(seriesId);
        seriesDao.delete(seriesId);
    }
    
    public void episodeWatched(Series series) {
        List<Season> seasons = new ArrayList<>();
        for (Season season : series.getSeasons()) {
            if (season.isWatched()) {
                seasons.add(season);
            } else {
                List<Episode> episodes = new ArrayList<>();
                for (Episode episode : season.getEpisodes()) {
                    if (episode.isWatched()) {
                        episodes.add(episode);
                    } else {
                        episode.setWatched(true);
                        episodeDao.update(episode);
                        episodes.add(episode);
                        break;
                    }
                }
                if (episodes.size() == season.getEpisodes().size()) {
                    season.setWatched(true);
                    seasonDao.update(season);
                    seasons.add(season);
                }
                break;
            }
        }

        if (seasons.size() == series.getSeasons().size() && !series.isInProduction()) {
            series.setWatched(true);
            seriesDao.update(series, seriesDao.getHighestListPosition(true));
        }
    }

    public void episodeClicked(Episode episode) {
        episode.setWatched(!episode.isWatched());
        episodeDao.update(episode);

        if (!episode.isWatched()) {
            Series series = getSeriesById(episode.getSeriesId());
            series.setWatched(false);
            seriesDao.update(series, seriesDao.getHighestListPosition(false));
            for (Season season : series.getSeasons()) {
                if (season.getId() == episode.getSeasonId()) {
                    season.setWatched(false);
                    seasonDao.update(season);
                    break;
                }
            }

        }
    }

    public void importSeries(Series series) {
        Series oldSeries = getSeriesById(series.getId());
        if (oldSeries == null) {
            seriesDao.create(series);
            for (Season season : series.getSeasons()) {
                seasonDao.create(season, series.getId());
                for (Episode episode : season.getEpisodes()) {
                    episodeDao.create(episode, series.getId(), season.getId());
                }
            }
        } else {
            seriesDao.update(series, series.getListPosition());
            for (Season season : series.getSeasons()) {
                seasonDao.update(season);
                for (Episode episode : season.getEpisodes()) {
                    episodeDao.update(episode);
                }
            }
        }
    }
    
    public void update(Series series) {
        Series oldSeries = getSeriesById(series.getId());
        int newPosition = getNewPosition(series, oldSeries);
        series.setWatched(oldSeries.isWatched());

        seriesDao.update(series, newPosition);

        for (Season season : series.getSeasons()) {
            update(season, series.getId());
        }

    }

    public void updatePosition(Series series) {
        Series oldSeries = getSeriesById(series.getId());
        int newPosition = getNewPosition(series, oldSeries);
        seriesDao.update(series, newPosition);
    }

    private void update(Season season, long seriesId) {
        String selection = SeasonEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(season.getId())};

        season.setSeriesId(seriesId);

        List<Season> seasons = seasonDao.read(selection, selectionArgs);
        if (seasons.size() > 0) {
            Season oldSeason = seasonDao.read(selection, selectionArgs).get(0);
            season.setWatched(oldSeason.isWatched());
            seasonDao.update(season);
        } else {
            seasonDao.create(season, seriesId);
        }


        for (Episode episode : season.getEpisodes()) {
            update(episode, seriesId, season.getId());
        }
    }

    private void update(Episode episode, long seriesId, long seasonId) {
        String selection = EpisodeEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(episode.getId())};

        episode.setSeriesId(seriesId);
        episode.setSeasonId(seasonId);

        List<Episode> episodes = episodeDao.read(selection, selectionArgs);

        if (episodes.size() > 0) {
            Episode oldEpisode = episodes.get(0);
            episode.setWatched(oldEpisode.isWatched());
            episodeDao.update(episode);
        } else {
            episodeDao.create(episode);
        }

    }

    private int getNewPosition(Series updatedSeries, Series oldSeries) {
        if (updatedSeries.isWatched() == oldSeries.isWatched()) {
            return updatedSeries.getListPosition();
        }

        return seriesDao.getHighestListPosition(updatedSeries.isWatched());
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
}
