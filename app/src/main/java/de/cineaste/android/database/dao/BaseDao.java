package de.cineaste.android.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import de.cineaste.android.util.Constants;

public abstract class BaseDao extends SQLiteOpenHelper {

	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";
	private static final String REAL_TYPE = " REAL";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_USER_ENTRIES =
			"CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME + " (" +
					UserEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
					UserEntry.COLUMN_USER_NAME + TEXT_TYPE +
					" )";
	private static final String SQL_CREATE_MOVIE_ENTRIES =
			"CREATE TABLE IF NOT EXISTS " + MovieEntry.TABLE_NAME + " (" +
					MovieEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
					MovieEntry.COLUMN_MOVIE_TITLE + TEXT_TYPE + COMMA_SEP +
					MovieEntry.COlUMN_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
					MovieEntry.COLUMN_RUNTIME + INTEGER_TYPE + COMMA_SEP +
					MovieEntry.COLUMN_VOTE_AVERAGE + REAL_TYPE + COMMA_SEP +
					MovieEntry.COLUMN_VOTE_COUNT + INTEGER_TYPE + COMMA_SEP +
					MovieEntry.COLUMN_MOVIE_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
					MovieEntry.COLUMN_MOVIE_WATCHED + INTEGER_TYPE + COMMA_SEP +
					MovieEntry.COLUMN_MOVIE_WATCHED_DATE + INTEGER_TYPE + COMMA_SEP +
					MovieEntry.COLUMN_MOVIE_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
					MovieEntry.COLUMN_MOVIE_LIST_POSITION + INTEGER_TYPE +
					" )";
	private static final String SQL_CREATE_SERIES_ENTRIES =
			"CREATE TABLE IF NOT EXISTS " + SeriesEntry.TABLE_NAME + " (" +
					SeriesEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_NAME + TEXT_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_VOTE_AVERAGE + REAL_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_VOTE_COUNT + INTEGER_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_IN_PRODUCTION + INTEGER_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_NUMBER_OF_EPISODES + INTEGER_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_NUMBER_OF_SEASONS + INTEGER_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_BACKDROP_PATH + TEXT_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_EPISODE + INTEGER_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_CURRENT_NUMBER_OF_SEASON + INTEGER_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_SERIES_WATCHED + INTEGER_TYPE + COMMA_SEP +
					SeriesEntry.COLUMN_SERIES_LIST_POSITION + INTEGER_TYPE +
					" )";
	private static final String SQL_CREATE_SEASON_ENTRIES =
			"CREATE TABLE IF NOT EXISTS " + SeasonEntry.TABLE_NAME + " (" +
					SeasonEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
					SeasonEntry.COLUMN_SEASON_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
					SeasonEntry.COLUMN_SEASON_EPISODE_COUNT + INTEGER_TYPE + COMMA_SEP +
					SeasonEntry.COLUMN_SEASON_POSTER_PATH + TEXT_TYPE + COMMA_SEP +
					SeasonEntry.COLUMN_SEASON_SEASON_NUMBER + INTEGER_TYPE + COMMA_SEP +
					SeasonEntry.COLUMN_SEASON_SERIES_ID + INTEGER_TYPE + " )"/*COMMA_SEP +
					" FOREIGN KEY (" + SeasonEntry.COLUMN_SEASON_SERIES_ID + ") REFERENCES " +
						SeasonEntry.TABLE_NAME + "(" + SeasonEntry._ID + "))"*/;

	private static final String SQL_CREATE_EPISODE_ENTRIES =
			"CREATE TABLE IF NOT EXISTS " + EpisodeEntry.TABLE_NAME + " (" +
					EpisodeEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
					EpisodeEntry.COLUMN_EPISODE_EPISODE_NUMBER + INTEGER_TYPE + COMMA_SEP +
					EpisodeEntry.COLUMN_EPISODE_NAME + TEXT_TYPE + COMMA_SEP +
					EpisodeEntry.COLUMN_EPISODE_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
					EpisodeEntry.COLUMN_EPISODE_SEASON_ID + INTEGER_TYPE + COMMA_SEP +
					EpisodeEntry.COLUMN_EPISODE_WATCHED + INTEGER_TYPE +
					" )";

	protected final SQLiteDatabase readDb;
	protected final SQLiteDatabase writeDb;

	private static final int DATABASE_VERSION = Constants.DATABASE_VERSION;
	private static final String DATABASE_NAME = Constants.DATABASE_NAME;

	public static abstract class UserEntry implements BaseColumns {

		public static final String TABLE_NAME = "user";
		public static final String COLUMN_USER_NAME = "userName";
	}

	public static abstract class MovieEntry implements BaseColumns {

		public static final String TABLE_NAME = "movie";
		public static final String COlUMN_POSTER_PATH = "posterPath";
		public static final String COLUMN_RUNTIME = "runtime";
		public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
		public static final String COLUMN_VOTE_COUNT = "voteCount";
		public static final String COLUMN_MOVIE_TITLE = "title";
		public static final String COLUMN_MOVIE_DESCRIPTION = "description";
		public static final String COLUMN_MOVIE_WATCHED = "watched";
		public static final String COLUMN_MOVIE_WATCHED_DATE = "watchedDate";
		public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";
		public static final String COLUMN_MOVIE_LIST_POSITION = "listPosition";
	}

	public static abstract class SeriesEntry implements BaseColumns {

		public static final String TABLE_NAME = "series";
		public static final String COLUMN_SERIES_NAME = "seriesName";
		static final String COLUMN_SERIES_VOTE_AVERAGE = "voteAverage";
		static final String COLUMN_SERIES_VOTE_COUNT = "voteCount";
		static final String COLUMN_SERIES_DESCRIPTION = "description";
		public static final String COLUMN_SERIES_RELEASE_DATE = "releaseDate";
		static final String COLUMN_SERIES_IN_PRODUCTION = "inProduction";
		static final String COLUMN_SERIES_NUMBER_OF_EPISODES = "numberOfEpisodes";
		static final String COLUMN_SERIES_NUMBER_OF_SEASONS = "numberOfSeasons";
		static final String COLUMN_SERIES_POSTER_PATH = "posterPath";
		static final String COLUMN_SERIES_CURRENT_NUMBER_OF_EPISODE = "currentNumberOfEpisode";
		static final String COLUMN_SERIES_CURRENT_NUMBER_OF_SEASON = "currentNumberOfSeason";
		public static final String COLUMN_SERIES_SERIES_WATCHED = "seriesWatched";
		public static final String COLUMN_SERIES_LIST_POSITION = "listPosition";
		static final String COLUMN_SERIES_BACKDROP_PATH = "backdropPath";
	}

	public static abstract class SeasonEntry implements BaseColumns {

		static final String TABLE_NAME = "season";
		public static final String COLUMN_SEASON_SERIES_ID = "seriesId";
		public static final String COLUMN_SEASON_SEASON_NUMBER = "seasonNumber";
		static final String COLUMN_SEASON_RELEASE_DATE = "releaseDate";
		static final String COLUMN_SEASON_EPISODE_COUNT = "episodenCount";
		static final String COLUMN_SEASON_POSTER_PATH = "posterPath";
	}

	public static abstract class EpisodeEntry implements BaseColumns {
		static final String TABLE_NAME = "episode";
		static final String COLUMN_EPISODE_EPISODE_NUMBER = "episodeNumber";
		static final String COLUMN_EPISODE_NAME = "name";
		static final String COLUMN_EPISODE_DESCRIPTION = "description";
		public static final String COLUMN_EPISODE_SEASON_ID = "seasonId";
		static final String COLUMN_EPISODE_WATCHED = "watched";

	}

	public BaseDao(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.readDb = getReadableDatabase();
		this.writeDb = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_USER_ENTRIES);
		db.execSQL(SQL_CREATE_MOVIE_ENTRIES);
		db.execSQL(SQL_CREATE_SERIES_ENTRIES);
		db.execSQL(SQL_CREATE_SEASON_ENTRIES);
		db.execSQL(SQL_CREATE_EPISODE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			db.execSQL("ALTER TABLE " + MovieEntry.TABLE_NAME + " ADD COLUMN " + MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " " + TEXT_TYPE + ";");
		}

		if (oldVersion < 3) {
			db.execSQL("ALTER TABLE " + MovieEntry.TABLE_NAME + " ADD COLUMN " + MovieEntry.COLUMN_MOVIE_LIST_POSITION + " " + INTEGER_TYPE + ";");
		}

		if (oldVersion < 4) {
			db.execSQL(SQL_CREATE_SERIES_ENTRIES);
			db.execSQL(SQL_CREATE_SEASON_ENTRIES);
			db.execSQL(SQL_CREATE_EPISODE_ENTRIES);
		}
	}
}
