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
        @Volatile
        private var instance: UserDbHelper? = null

        fun getInstance(context: Context): UserDbHelper {
            return instance ?: synchronized(this) {
                val dbHelper = UserDbHelper(context)
                instance = dbHelper
                return dbHelper
            }
        }
    }
}