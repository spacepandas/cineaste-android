package de.cineaste.android.database.dbHelper

import android.content.ContentValues
import android.content.Context
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.entity.User

class UserDbHelper private constructor(context: Context) : BaseDao(context) {

    val user: User?
        get() {

            val projection = arrayOf(UserEntry.ID, UserEntry.COLUMN_USER_NAME)

            val c = readDb.query(
                UserEntry.TABLE_NAME,
                projection,
                null, null, null, null, null, null
            )

            var user: User? = null

            if (c.moveToFirst()) {
                do {
                    user = User()
                    user.userName = c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_USER_NAME))
                } while (c.moveToNext())
            }
            c.close()
            return user
        }

    fun createUser(user: User) {
        val values = ContentValues()
        values.put(UserEntry.COLUMN_USER_NAME, user.userName)

        writeDb.insert(UserEntry.TABLE_NAME, null, values)
    }

    companion object {

        private var mInstance: UserDbHelper? = null

        fun getInstance(context: Context): UserDbHelper {
            if (mInstance == null) {
                mInstance = UserDbHelper(context)
            }

            return mInstance!!
        }
    }
}