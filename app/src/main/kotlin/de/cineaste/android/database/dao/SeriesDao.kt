package de.cineaste.android.database.dao

import android.content.ContentValues
import android.content.Context
import de.cineaste.android.entity.series.Series

class SeriesDao private constructor(context: Context) : BaseDao(context) {

    fun reorder(statement: String) {
        writeDb.execSQL(statement)
    }

    fun create(series: Series) {
        val values = ContentValues()
        values.put(SeriesEntry.ID, series.id)
        values.put(SeriesEntry.COLUMN_SERIES_NAME, series.name)
        values.put(SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE, series.voteAverage)
        values.put(SeriesEntry.COLUMN_SERIES_VOTE_COUNT, series.voteCount)
        values.put(SeriesEntry.COLUMN_SERIES_DESCRIPTION, series.description)
        if (series.releaseDate == null) {
            values.put(SeriesEntry.COLUMN_SERIES_RELEASE_DATE, "")
        } else {
            values.put(SeriesEntry.COLUMN_SERIES_RELEASE_DATE, sdf.format(series.releaseDate))
        }
        values.put(SeriesEntry.COLUMN_SERIES_IN_PRODUCTION, if (series.isInProduction) 1 else 0)
        values.put(SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES, series.numberOfEpisodes)
        values.put(SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS, series.numberOfSeasons)
        values.put(SeriesEntry.COLUMN_SERIES_POSTER_PATH, series.posterPath)
        values.put(SeriesEntry.COLUMN_SERIES_BACKDROP_PATH, series.backdropPath)
        values.put(SeriesEntry.COLUMN_SERIES_SERIES_WATCHED, if (series.isWatched) 1 else 0)
        values.put(
            SeriesEntry.COLUMN_SERIES_LIST_POSITION,
            getHighestListPosition(series.isWatched)
        )

        writeDb.insert(SeriesEntry.TABLE_NAME, null, values)
    }

    fun read(selection: String?, selectionArgs: Array<String>?, orderBy: String?): List<Series> {
        val series = ArrayList<Series>()

        val projection = arrayOf(
            SeriesEntry.ID,
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
            SeriesEntry.COLUMN_SERIES_SERIES_WATCHED,
            SeriesEntry.COLUMN_SERIES_LIST_POSITION
        )

        val c = readDb.query(
            SeriesEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs, null, null,
            orderBy, null
        )

        if (c.moveToFirst()) {
            do {
                val currentSeries = Series()
                currentSeries.id = c.getLong(c.getColumnIndexOrThrow(SeriesEntry.ID))
                currentSeries.name =
                    c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_NAME))
                currentSeries.voteAverage =
                    c.getDouble(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE))
                currentSeries.voteCount =
                    c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_VOTE_COUNT))
                currentSeries.description =
                    c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_DESCRIPTION))
                try {
                    currentSeries.releaseDate =
                        sdf.parse(c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_RELEASE_DATE)))
                } catch (ex: Exception) {
                    currentSeries.releaseDate = null
                }

                currentSeries.isInProduction =
                    c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_IN_PRODUCTION)) > 0
                currentSeries.numberOfEpisodes =
                    c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES))
                currentSeries.numberOfSeasons =
                    c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS))
                currentSeries.posterPath =
                    c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_POSTER_PATH))
                currentSeries.backdropPath =
                    c.getString(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_BACKDROP_PATH))
                currentSeries.isWatched =
                    c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_SERIES_WATCHED)) > 0
                currentSeries.listPosition =
                    c.getInt(c.getColumnIndexOrThrow(SeriesEntry.COLUMN_SERIES_LIST_POSITION))

                series.add(currentSeries)
            } while (c.moveToNext())
        }
        c.close()
        return series
    }

    fun update(series: Series, listPosition: Int) {
        val values = ContentValues()
        values.put(SeriesEntry.ID, series.id)
        values.put(SeriesEntry.COLUMN_SERIES_NAME, series.name)
        values.put(SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE, series.voteAverage)
        values.put(SeriesEntry.COLUMN_SERIES_VOTE_COUNT, series.voteCount)
        values.put(SeriesEntry.COLUMN_SERIES_DESCRIPTION, series.description)
        if (series.releaseDate == null) {
            values.put(SeriesEntry.COLUMN_SERIES_RELEASE_DATE, "")
        } else {
            values.put(SeriesEntry.COLUMN_SERIES_RELEASE_DATE, sdf.format(series.releaseDate))
        }
        values.put(SeriesEntry.COLUMN_SERIES_IN_PRODUCTION, if (series.isInProduction) 1 else 0)
        values.put(SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES, series.numberOfEpisodes)
        values.put(SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS, series.numberOfSeasons)
        values.put(SeriesEntry.COLUMN_SERIES_POSTER_PATH, series.posterPath)
        values.put(SeriesEntry.COLUMN_SERIES_BACKDROP_PATH, series.backdropPath)
        values.put(SeriesEntry.COLUMN_SERIES_SERIES_WATCHED, if (series.isWatched) 1 else 0)
        values.put(SeriesEntry.COLUMN_SERIES_LIST_POSITION, listPosition)

        val selection = SeriesEntry.ID + " LIKE ?"
        val selectionArgs = arrayOf(series.id.toString())

        writeDb.update(SeriesEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    fun delete(id: Long) {
        writeDb.delete(SeriesEntry.TABLE_NAME, SeriesEntry.ID + " = ?", arrayOf(id.toString() + ""))
    }

    fun getHighestListPosition(watchState: Boolean): Int {
        var highestPos = 0

        val projection = arrayOf("MAX(" + SeriesEntry.COLUMN_SERIES_LIST_POSITION + ") AS POS")

        val selection = SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + " = ?"

        val selectionArg = if (watchState) "1" else "0"

        val c = writeDb.query(
            SeriesEntry.TABLE_NAME,
            projection,
            selection,
            arrayOf(selectionArg), null, null, null
        )
        if (c.moveToFirst()) {
            do {
                highestPos = c.getInt(c.getColumnIndex("POS"))
            } while (c.moveToNext())
        }

        c.close()
        return highestPos
    }

    companion object {

        private var instance: SeriesDao? = null

        fun getInstance(context: Context): SeriesDao {
            if (instance == null) {
                instance = SeriesDao(context)
            }

            return instance!!
        }
    }
}
