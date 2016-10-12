package de.cineaste.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import de.cineaste.android.Constants;

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
					MovieEntry.COLUMN_MOVIE_WATCHED_DATE + INTEGER_TYPE +
					" )";
	private static final String SQL_DELETE_USER_ENTRIES =
			"DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
	private static final String SQL_DELETE_MOVIE_ENTRIES =
			"DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;

	final SQLiteDatabase readDb;
	final SQLiteDatabase writeDb;

	private static final int DATABASE_VERSION = Constants.DATABASE_VERSION;
	private static final String DATABASE_NAME = Constants.DATABASE_NAME;

	static abstract class UserEntry implements BaseColumns {

		static final String TABLE_NAME = "user";
		static final String COLUMN_USER_NAME = "userName";
	}

	public static abstract class MovieEntry implements BaseColumns {

		static final String TABLE_NAME = "movie";
		static final String COlUMN_POSTER_PATH = "posterPath";
		static final String COLUMN_RUNTIME = "runtime";
		static final String COLUMN_VOTE_AVERAGE = "voteAverage";
		static final String COLUMN_VOTE_COUNT = "voteCount";
		static final String COLUMN_MOVIE_TITLE = "title";
		static final String COLUMN_MOVIE_DESCRIPTION = "description";
		static final String COLUMN_MOVIE_WATCHED = "watched";
		static final String COLUMN_MOVIE_WATCHED_DATE = "watchedDate";
	}

	BaseDao(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.readDb = getReadableDatabase();
		this.writeDb = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_USER_ENTRIES);
		db.execSQL(SQL_CREATE_MOVIE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int i, int i1) {
		db.execSQL(SQL_DELETE_USER_ENTRIES);
		db.execSQL(SQL_DELETE_MOVIE_ENTRIES);
		onCreate(db);
	}
}
