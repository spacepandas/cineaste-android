package de.cineaste.android.database.dbHelper

import android.content.Context
import de.cineaste.android.database.CineasteDb
import de.cineaste.android.database.room.UserDao
import de.cineaste.android.entity.User

class UserDbHelper private constructor(context: Context) {

    private val userDao: UserDao = CineasteDb.getDatabase(context).userDao()
    val user: User? = userDao.getUser()

    fun createUser(user: User) {
        userDao.insert(user)
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