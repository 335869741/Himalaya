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
        const val ALBUM_SUBSCRIBE_DATABASE_VERSION = 3

        private var instance: AlbumSubscribeDatabase? = null

        fun getAlbumSubscribeDatabase(): AlbumSubscribeDatabase {
            if (instance == null) {
                synchronized(AlbumSubscribeDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            BaseApplication.getContext(),
                            AlbumSubscribeDatabase::class.java,
                            "user_album_subscribe_database.db"
                        )
                            .addMigrations(
                                migration_1_2,
                                migration_2_3
                            ).build()
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

        @JvmStatic
        val migration_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(migration_2_3_temp_sql)
                database.execSQL(migration_2_3_insert_sql)
                database.execSQL("DROP TABLE album_subscribe")
                database.execSQL("ALTER TABLE album_temp RENAME TO album_subscribe")
            }
        }
        val migration_2_3_temp_sql = """
            CREATE TABLE album_temp(
                id INTEGER PRIMARY KEY NOT NULL,
                title TEXT NOT NULL,
                info TEXT NOT NULL,
                cover_url TEXT DEFAULT NULL,
                track_count INTEGER NOt NULL DEFAULT 0,
                play_count INTEGER NOt NULL DEFAULT 0,
                album_id INTEGER NOt NULL DEFAULT 0
            )
        """.trimIndent()
        val migration_2_3_insert_sql = """
            INSERT INTO album_temp(
                id, title, info, cover_url, track_count, play_count, album_id)
            SELECT id, title, info, cover_url, track_count, play_count, id 
            FROM album_subscribe
        """.trimIndent()
    }
}

