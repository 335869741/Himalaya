package zzz.bing.himalaya.db.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import zzz.bing.himalaya.BaseApplication
import zzz.bing.himalaya.db.dao.AlbumSubscribeDao
import zzz.bing.himalaya.db.entity.AlbumSubscribe

@Database(
    entities = [AlbumSubscribe::class],
    version = AlbumSubscribeDatabase.ALBUM_SUBSCRIBE_DATABASE_VERSION,
    exportSchema = true
)
abstract class AlbumSubscribeDatabase : RoomDatabase() {
    abstract fun userAlbumSubscribeDao(): AlbumSubscribeDao

    companion object {
        const val ALBUM_SUBSCRIBE_DATABASE_VERSION = 2

        private var instance: AlbumSubscribeDatabase? = null

        fun getSearchHistoryDatabase(): AlbumSubscribeDatabase {
            if (instance == null) {
                synchronized(AlbumSubscribeDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            BaseApplication.getContext(),
                            AlbumSubscribeDatabase::class.java,
                            "user_album_subscribe_database.db"
                        )
                            .addMigrations(migration_1_2)
                            .build()
                    }
                }
            }
            return instance!!
        }

        @JvmStatic
        val migration_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE album_subscribe ADD COLUMN track_count INTEGER NOt NULL DEFAULT 0")
                database.execSQL("ALTER TABLE album_subscribe ADD COLUMN play_count INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}