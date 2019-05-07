package de.cineaste.android.database.dao

import android.content.ContentValues
import android.content.Context
import de.cineaste.android.entity.series.Season
import java.text.ParseException

class SeasonDao private constructor(context: Context) : BaseDao(context) {

    fun executeCustomSql(sql: String) {
        writeDb.execSQL(sql)
    }

    fun create(season: Season, seriesId: Long) {
        if (season.seasonNumber == 0) {
            return
            // exclude specials if present
        }
        val values = ContentValues()
        values.put(SeasonEntry.ID, season.id)
        if (season.releaseDate == null) {
            values.put(SeasonEntry.COLUMN_SEASON_RELEASE_DATE, "")
        } else {
            values.put(SeasonEntry.COLUMN_SEASON_RELEASE_DATE, sdf.format(season.releaseDate))
        }
        values.put(SeasonEntry.COLUMN_SEASON_EPISODE_COUNT, season.episodeCount)
        values.put(SeasonEntry.COLUMN_SEASON_POSTER_PATH, season.posterPath)
        values.put(SeasonEntry.COLUMN_SEASON_SEASON_NUMBER, season.seasonNumber)
        values.put(SeasonEntry.COLUMN_SEASON_SERIES_ID, seriesId)
        values.put(SeasonEntry.COLUMN_SEASON_WATCHED, if (season.isWatched) 1 else 0)

        writeDb.insert(SeasonEntry.TABLE_NAME, null, values)
    }

    fun read(selection: String, selectionArgs: Array<String>): List<Season> {
        val seasons = ArrayList<Season>()

        val projection = arrayOf(
            SeasonEntry.ID,
            SeasonEntry.COLUMN_SEASON_RELEASE_DATE,
            SeasonEntry.COLUMN_SEASON_EPISODE_COUNT,
            SeasonEntry.COLUMN_SEASON_POSTER_PATH,
            SeasonEntry.COLUMN_SEASON_SEASON_NUMBER,
            SeasonEntry.COLUMN_SEASON_SERIES_ID,
            SeasonEntry.COLUMN_SEASON_WATCHED
        )

        val c = readDb.query(
            SeasonEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs, null, null,
            SeasonEntry.COLUMN_SEASON_SEASON_NUMBER + " ASC", null
        )

        if (c.moveToFirst()) {
            do {
                val currentSeason = Season()
                currentSeason.id = c.getLong(c.getColumnIndexOrThrow(SeasonEntry.ID))
                try {
                    currentSeason.releaseDate =
                        sdf.parse(c.getString(c.getColumnIndexOrThrow(SeasonEntry.COLUMN_SEASON_RELEASE_DATE)))
                } catch (ex: ParseException) {
                    currentSeason.releaseDate = null
                }

                currentSeason.episodeCount =
                    c.getInt(c.getColumnIndexOrThrow(SeasonEntry.COLUMN_SEASON_EPISODE_COUNT))
                currentSeason.posterPath =
                    c.getString(c.getColumnIndexOrThrow(SeasonEntry.COLUMN_SEASON_POSTER_PATH))
                currentSeason.seasonNumber =
                    c.getInt(c.getColumnIndexOrThrow(SeasonEntry.COLUMN_SEASON_SEASON_NUMBER))
                currentSeason.seriesId =
                    c.getLong(c.getColumnIndexOrThrow(SeasonEntry.COLUMN_SEASON_SERIES_ID))
                currentSeason.isWatched =
                    c.getInt(c.getColumnIndexOrThrow(SeasonEntry.COLUMN_SEASON_WATCHED)) > 0

                seasons.add(currentSeason)
            } while (c.moveToNext())
        }
        c.close()
        return seasons
    }

    fun update(season: Season) {
        if (season.seasonNumber == 0) {
            return
        }
        val values = ContentValues()
        values.put(SeasonEntry.ID, season.id)

        if (season.releaseDate == null) {
            values.put(SeasonEntry.COLUMN_SEASON_RELEASE_DATE, "")
        } else {
            values.put(SeasonEntry.COLUMN_SEASON_RELEASE_DATE, sdf.format(season.releaseDate))
        }
        values.put(SeasonEntry.COLUMN_SEASON_EPISODE_COUNT, season.episodeCount)
        values.put(SeasonEntry.COLUMN_SEASON_POSTER_PATH, season.posterPath)
        values.put(SeasonEntry.COLUMN_SEASON_SEASON_NUMBER, season.seasonNumber)
        values.put(SeasonEntry.COLUMN_SEASON_SERIES_ID, season.seriesId)
        values.put(SeasonEntry.COLUMN_SEASON_WATCHED, if (season.isWatched) 1 else 0)

        val selection = SeasonEntry.ID + " LIKE ?"
        val selectionArgs = arrayOf(season.id.toString())

        writeDb.update(SeasonEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    fun deleteBySeriesId(id: Long) {
        writeDb.delete(
            SeasonEntry.TABLE_NAME,
            SeasonEntry.COLUMN_SEASON_SERIES_ID + " = ?",
            arrayOf(id.toString() + "")
        )
    }

    companion object {

        private var instance: SeasonDao? = null

        fun getInstance(context: Context): SeasonDao {
            if (instance == null) {
                instance = SeasonDao(context)
            }

            return instance!!
        }
    }
}
