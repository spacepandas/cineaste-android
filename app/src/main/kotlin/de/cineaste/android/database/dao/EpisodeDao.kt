package de.cineaste.android.database.dao

import android.content.ContentValues
import android.content.Context
import de.cineaste.android.entity.series.Episode

class EpisodeDao private constructor(context: Context) : BaseDao(context) {

    fun executeCustomSql(sql: String) {
        writeDb.execSQL(sql)
    }

    fun create(episode: Episode) {
        val values = ContentValues()
        values.put(EpisodeEntry.ID, episode.id)
        values.put(EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER, episode.episodeNumber)
        values.put(EpisodeEntry.COLUMN_EPISODE_NAME, episode.name)
        values.put(EpisodeEntry.COLUMN_EPISODE_DESCRIPTION, episode.description)
        values.put(EpisodeEntry.COLUMN_EPISODE_SEASON_ID, episode.seasonId)
        values.put(EpisodeEntry.COLUMN_EPISODE_WATCHED, if (episode.isWatched) 1 else 0)

        writeDb.insert(
            EpisodeEntry.TABLE_NAME,
            null, values
        )
    }

    fun create(episode: Episode, seriesId: Long, seasonId: Long) {
        val values = ContentValues()
        values.put(EpisodeEntry.ID, episode.id)
        values.put(EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER, episode.episodeNumber)
        values.put(EpisodeEntry.COLUMN_EPISODE_NAME, episode.name)
        values.put(EpisodeEntry.COLUMN_EPISODE_DESCRIPTION, episode.description)
        values.put(EpisodeEntry.COLUMN_EPISODE_SERIES_ID, seriesId)
        values.put(EpisodeEntry.COLUMN_EPISODE_SEASON_ID, seasonId)
        values.put(EpisodeEntry.COLUMN_EPISODE_WATCHED, if (episode.isWatched) 1 else 0)

        writeDb.insert(EpisodeEntry.TABLE_NAME, null, values)
    }

    fun read(selection: String, selectionArgs: Array<String>): List<Episode> {
        val episodes = ArrayList<Episode>()

        val projection = arrayOf(
            EpisodeEntry.ID,
            EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER,
            EpisodeEntry.COLUMN_EPISODE_NAME,
            EpisodeEntry.COLUMN_EPISODE_DESCRIPTION,
            EpisodeEntry.COLUMN_EPISODE_SERIES_ID,
            EpisodeEntry.COLUMN_EPISODE_SEASON_ID,
            EpisodeEntry.COLUMN_EPISODE_WATCHED
        )

        val c = readDb.query(
            EpisodeEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs, null, null,
            EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER + " ASC", null
        )

        if (c.moveToFirst()) {
            do {
                val currentEpisode = Episode()
                currentEpisode.id = c.getLong(c.getColumnIndexOrThrow(EpisodeEntry.ID))
                currentEpisode.episodeNumber =
                    c.getInt(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER))
                currentEpisode.name =
                    c.getString(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_NAME))
                currentEpisode.description =
                    c.getString(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_DESCRIPTION))
                currentEpisode.seriesId =
                    c.getLong(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_SERIES_ID))
                currentEpisode.seasonId =
                    c.getLong(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_SEASON_ID))
                currentEpisode.isWatched =
                    c.getInt(c.getColumnIndexOrThrow(EpisodeEntry.COLUMN_EPISODE_WATCHED)) > 0

                episodes.add(currentEpisode)
            } while (c.moveToNext())
        }
        c.close()
        return episodes
    }

    fun update(episode: Episode) {
        val values = ContentValues()
        values.put(EpisodeEntry.ID, episode.id)
        values.put(EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER, episode.episodeNumber)
        values.put(EpisodeEntry.COLUMN_EPISODE_NAME, episode.name)
        values.put(EpisodeEntry.COLUMN_EPISODE_DESCRIPTION, episode.description)
        values.put(EpisodeEntry.COLUMN_EPISODE_SERIES_ID, episode.seriesId)
        values.put(EpisodeEntry.COLUMN_EPISODE_SEASON_ID, episode.seasonId)
        values.put(EpisodeEntry.COLUMN_EPISODE_WATCHED, if (episode.isWatched) 1 else 0)

        val selection = EpisodeEntry.ID + " LIKE ?"
        val selectionArgs = arrayOf(episode.id.toString())

        writeDb.update(EpisodeEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    fun deleteBySeriesId(seriesId: Long) {
        writeDb.delete(
            EpisodeEntry.TABLE_NAME,
            EpisodeEntry.COLUMN_EPISODE_SERIES_ID + " = ?",
            arrayOf(seriesId.toString() + "")
        )
    }

    companion object {

        private var instance: EpisodeDao? = null

        fun getInstance(context: Context): EpisodeDao {
            if (instance == null) {
                instance = EpisodeDao(context)
            }

            return instance!!
        }
    }
}
