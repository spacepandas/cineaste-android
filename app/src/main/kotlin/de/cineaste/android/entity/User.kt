package de.cineaste.android.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @ColumnInfo(name = ID)
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userName: String = ""
) {
    companion object {
        const val TABLE_NAME = "user"
        const val ID = "_id"
        const val USER_NAME = "userName"

        const val SQL_CREATE_USER_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " +
                    "$TABLE_NAME ( " +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$USER_NAME TEXT" +
                    ")"
    }
}