package de.cineaste.android.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.cineaste.android.database.dao.BaseDao
import de.cineaste.android.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM ${BaseDao.UserEntry.TABLE_NAME} LIMIT 1")
    fun getUser(): User

    @Insert
    fun insert(user: User)
}