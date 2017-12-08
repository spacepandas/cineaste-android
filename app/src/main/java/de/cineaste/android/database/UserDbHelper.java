package de.cineaste.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import de.cineaste.android.entity.User;

public class UserDbHelper extends BaseDao {

	private static UserDbHelper mInstance;

	private UserDbHelper(Context context) {
		super(context);
	}

	public static UserDbHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new UserDbHelper(context);
		}

		return mInstance;
	}


	public void createUser(User user) {
		ContentValues values = new ContentValues();
		values.put(UserEntry.COLUMN_USER_NAME, user.getUserName());

		writeDb.insert(UserEntry.TABLE_NAME, null, values);
	}

	public User getUser() {

		String[] projection = {
				UserEntry._ID,
				UserEntry.COLUMN_USER_NAME
		};

		Cursor c = readDb.query(
				UserEntry.TABLE_NAME,
				projection,
				null,
				null,
				null,
				null,
				null,
				null);

		User user = null;

		if (c.moveToFirst()) {
			do {
				user = new User();
				user.setUserName(
						c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_USER_NAME)));
			} while (c.moveToNext());
		}
		c.close();
		return user;
	}
}