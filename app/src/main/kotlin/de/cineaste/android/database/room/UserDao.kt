package de.cineaste.android.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.cineaste.android.entity.User
import de.cineaste.android.entity.User.Companion.TABLE_NAME

@Dao
interface UserDao {

    @Query("SELECT * FROM $TABLE_NAME LIMIT 1")
    fun getUser(): User

    @Insert
    fun insert(user: User)
}