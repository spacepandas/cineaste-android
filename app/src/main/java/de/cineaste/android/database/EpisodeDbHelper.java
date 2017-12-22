package de.cineaste.android.database;

import android.content.Context;

import java.util.List;

import de.cineaste.android.entity.Episode;

public class EpisodeDbHelper {

    private static EpisodeDbHelper instance;

    private final EpisodeDao episodeDao;

    private EpisodeDbHelper(Context context) {
        this.episodeDao = EpisodeDao.getInstance(context);
    }

    public static EpisodeDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new EpisodeDbHelper(context);
        }

        return instance;
    }

    public List<Episode> readAllEpisodesOfSeason(long seasonId) {
        String selection = BaseDao.EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = ?";
        String[] selectionArgs = {String.valueOf(seasonId)};

        return episodeDao.read(selection, selectionArgs);
    }

    public void createOrUpdate(Episode episode) {
        String selection = BaseDao.EpisodeEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(episode.getId())};
        List<Episode> episodeList = episodeDao.read(selection, selectionArgs);

        if (!episodeList.isEmpty()) {
            episode.setWatched(episodeList.get(0).isWatched());
            update(episode);
        } else {
            episodeDao.create(episode);
        }
    }

    public void updateWatchState(Episode episode) {
        String selection = BaseDao.EpisodeEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(episode.getId())};
        List<Episode> episodeList = episodeDao.read(selection, selectionArgs);

        if (!episodeList.isEmpty()) {
            update(episode);
        } else {
            episodeDao.create(episode);
        }
    }
    public void delete(Episode episode) {
        episodeDao.delete(episode.getId());
    }

    private void update(Episode episode) {
        episodeDao.update(episode);
    }
}
