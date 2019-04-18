package de.cineaste.android.database.dbHelper

import android.content.Context
import de.cineaste.android.database.room.UserDao
import de.cineaste.android.db
import de.cineaste.android.entity.User
import kotlinx.coroutines.GlobalScope

class UserDbHelper private constructor(context: Context) {

    private val userDao: UserDao = db!!.userDao()
    val user: User?
        get() {
            GlobalScope.let { return userDao.getUser() }
        }

    fun createUser(user: User) {
        GlobalScope.let { userDao.insert(user) }
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