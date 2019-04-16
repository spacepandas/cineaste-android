package de.cineaste.android.entity.movie

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import de.cineaste.android.database.dao.BaseDao
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = BaseDao.MovieEntry.TABLE_NAME)
data class MovieEntity(
    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var posterPath: String? = "",
    var title: String = "",
    var runtime: Int = 0,
    var voteAverage: Double = 0.toDouble(),
    var voteCount: Int = 0,
    var description: String = "",
    var watched: Boolean = false,
    var watchedDate: Long? = null,
    var releaseDate: String? = null,
    var listPosition: Int = 0
)

fun MovieEntity.toModel() =
    Movie(
        id,
        posterPath,
        title,
        runtime,
        voteAverage,
        voteCount,
        description,
        watched,
        watchedDate?.let {
            Date(it)
        },
        releaseDate?.let {
            SimpleDateFormat("dd.MM.yyyy-HH:mm", Locale.ENGLISH).parse(it)
        },
        listPosition
    )