package de.cineaste.android.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.series.Episode;

public class EpisodeDao extends BaseDao {

    private static EpisodeDao instance;

    private EpisodeDao(Context context) {
        super(context);
    }

    public static EpisodeDao getInstance(Context context) {
        if (instance == null) {
            instance = new EpisodeDao(context);
        }

        return instance;
    }

    public void create(Episode episode) {
        ContentValues values = new ContentValues();
        values.put(EpisodeEntry._ID, episode.getId());
        values.put(EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER, episode.getEpisodeNumber());
        values.put(EpisodeEntry.COLUMN_EPISODE_NAME, episode.getName());
        values.put(EpisodeEntry.COLUMN_EPISODE_DESCRIPTION, episode.getDescription());
        values.put(EpisodeEntry.COLUMN_EPISODE_SEASON_ID, episode.getSeasonId());
        values.put(EpisodeEntry.COLUMN_EPISODE_WATCHED, episode.isWatched() ? 1 : 0);

        writeDb.insert(EpisodeEntry.TABLE_NAME, null, values);
    }

    public List<Episode> read(String selection, String[] selectionArgs) {
        List<Episode> episodes = new ArrayList<>();

        String[] projection = {
                EpisodeEntry._ID,
                EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER,
                EpisodeEntry.COLUMN_EPISODE_NAME,
                EpisodeEntry.COLUMN_EPISODE_DESCRIPTION,
                EpisodeEntry.COLUMN_EPISODE_SEASON_ID,
                EpisodeEntry.COLUMN_EPISODE_WATCHED
        };

        Cursor c = readDb.query(
                EpisodeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER + " ASC",
                null);

        if (c.moveToFirst()) {
            do {
                Episode currentEpisode = new Episode();
                currentEpisode.setId(
                        c.getLong(c.getColumnIndexOrThrow(EpisodeEntry._ID)));
                currentEpisode.setEpisodeNumber(
                        c.getInt(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER)));
                currentEpisode.setName(
                        c.getString(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_NAME)));
                currentEpisode.setDescription(
                        c.getString(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_DESCRIPTION)));
                currentEpisode.setSeasonId(
                        c.getLong(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_SEASON_ID)));
                currentEpisode.setWatched(
                        c.getInt(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_WATCHED)) > 0);

                episodes.add(currentEpisode);
            } while (c.moveToNext());
        }
        c.close();
        return episodes;
    }

    public void update(Episode episode) {
        ContentValues values = new ContentValues();
        values.put(EpisodeEntry._ID, episode.getId());
        values.put(EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER, episode.getEpisodeNumber());
        values.put(EpisodeEntry.COLUMN_EPISODE_NAME, episode.getName());
        values.put(EpisodeEntry.COLUMN_EPISODE_DESCRIPTION, episode.getDescription());
        values.put(EpisodeEntry.COLUMN_EPISODE_SEASON_ID, episode.getSeasonId());
        values.put(EpisodeEntry.COLUMN_EPISODE_WATCHED, episode.isWatched() ? 1 : 0);

        String selection = BaseDao.EpisodeEntry._ID + " LIKE ?";
        String[] selectionArgs = {String.valueOf(episode.getId())};

        writeDb.update(EpisodeEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateWatchedStateForSeason(long seasonId, boolean watchstate) {
        int watchStatus;
        if (watchstate) {
            watchStatus = 1;
        } else {
            watchStatus = 0;
        }

        String sql = "UPDATE " + EpisodeEntry.TABLE_NAME +
                " SET " + EpisodeEntry.COLUMN_EPISODE_WATCHED + " = " + watchStatus +
                " WHERE " + EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = " + seasonId;

        writeDb.execSQL(sql);
    }

    public void delete(long id) {
        writeDb.delete(EpisodeEntry.TABLE_NAME, EpisodeEntry._ID + " = ?", new String[]{id + ""});
    }

    void deleteBySeasonId(long id) {
        writeDb.delete(EpisodeEntry.TABLE_NAME, EpisodeEntry.COLUMN_EPISODE_SEASON_ID + " = ?", new String[]{id + ""});
    }
}
