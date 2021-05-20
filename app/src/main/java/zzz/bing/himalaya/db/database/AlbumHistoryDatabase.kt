package zzz.bing.himalaya.db.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import zzz.bing.himalaya.BaseApplication
import zzz.bing.himalaya.db.dao.AlbumHistoryDao
import zzz.bing.himalaya.db.entity.AlbumHistory

@Database(
    entities = [AlbumHistory::class],
    version = AlbumHistoryDatabase.ALBUM_HISTORY_DATABASE_VERSION,
    exportSchema = true
)
abstract class AlbumHistoryDatabase : RoomDatabase() {
    abstract fun userAlbumHistoryDao(): AlbumHistoryDao

    companion object {
        const val ALBUM_HISTORY_DATABASE_VERSION = 1

        private var instance: AlbumHistoryDatabase? = null

        fun getAlbumHistoryDatabase(): AlbumHistoryDatabase {
            if (instance == null) {
                synchronized(AlbumHistoryDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            BaseApplication.getContext(),
                            AlbumHistoryDatabase::class.java,
                            "user_album_history_database.db"
                        ) // 版本迁移时不保留数据  // VERSION变更
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return instance!!
        }
    }
}

