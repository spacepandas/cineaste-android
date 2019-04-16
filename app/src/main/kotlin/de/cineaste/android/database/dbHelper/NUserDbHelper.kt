package de.cineaste.android.database.dbHelper

import android.content.Context
import de.cineaste.android.database.CineasteDb
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.database.room.UserDao
import de.cineaste.android.entity.User

class NUserDbHelper private constructor(context: Context) : BaseDao(context) {

    private val userDao: UserDao = CineasteDb.getDatabase(context).userDao()
    val user: User?
        get() = userDao.getUser()

    fun createUser(user: User) {
        userDao.insert(user)
    }

    companion object {

        private var mInstance: NUserDbHelper? = null

        fun getInstance(context: Context): NUserDbHelper {
            if (mInstance == null) {
                mInstance = NUserDbHelper(context)
            }

            return mInstance!!
        }
    }
}