package de.cineaste.android.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var userName: String = ""
)