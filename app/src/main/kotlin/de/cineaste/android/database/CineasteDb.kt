package de.cineaste.android.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import de.cineaste.android.database.room.*
import de.cineaste.android.entity.User
import de.cineaste.android.entity.User.Companion.SQL_CREATE_USER_ENTRIES
import de.cineaste.android.entity.movie.MovieEntity
import de.cineaste.android.entity.movie.MovieEntity.Companion.SQL_CREATE_MOVIE_ENTRIES
import de.cineaste.android.entity.series.EpisodeEntity
import de.cineaste.android.entity.series.EpisodeEntity.Companion.SQL_CREATE_EPISODE_ENTRIES
import de.cineaste.android.entity.series.SeasonEntity
import de.cineaste.android.entity.series.SeasonEntity.Companion.SQL_CREATE_SEASON_ENTRIES
import de.cineaste.android.entity.series.SeriesEntity
import de.cineaste.android.entity.series.SeriesEntity.Companion.SQL_CREATE_SERIES_ENTRIES
import de.cineaste.android.util.Constants

@Database(
    entities = [MovieEntity::class, User::class, SeriesEntity::class, SeasonEntity::class, EpisodeEntity::class],
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(DateTypeConverter::class)
abstract class CineasteDb : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun userDao(): UserDao
    abstract fun seriesDao(): SeriesDao
    abstract fun seasonDao(): SeasonDao
    abstract fun episodeDao(): EpisodeDao

    companion object {
        @Volatile
        private var INSTANCE: CineasteDb? = null

        fun getDatabase(context: Context): CineasteDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CineasteDb::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_0_1)
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}

val MIGRATION_0_1: Migration = object : Migration(0, 1) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(SQL_CREATE_MOVIE_ENTRIES)
        database.execSQL(SQL_CREATE_USER_ENTRIES)
    }
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE ${MovieEntity.TABLE_NAME} " +
                    "ADD COLUMN ${MovieEntity.RELEASE_DATE}  TEXT;"
        )
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE ${MovieEntity.TABLE_NAME} " +
                    "ADD COLUMN ${MovieEntity.LIST_POSITION}  INTEGER;"
        )
    }
}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(SQL_CREATE_SERIES_ENTRIES)
        database.execSQL(SQL_CREATE_SEASON_ENTRIES)
        database.execSQL(SQL_CREATE_EPISODE_ENTRIES)
    }
}

val MIGRATION_4_5: Migration = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
       // migration to room
    }
}