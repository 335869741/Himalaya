package zzz.bing.himalaya.db.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
        const val ALBUM_SUBSCRIBE_DATABASE_VERSION = 1

        private var instance: AlbumSubscribeDatabase? = null

        fun getSearchHistoryDatabase(): AlbumSubscribeDatabase {
            if (instance == null) {
                synchronized(AlbumSubscribeDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            BaseApplication.getContext(),
                            AlbumSubscribeDatabase::class.java,
                            "user_album_subscribe_database.db"
                        )   //版本迁移时不保留数据 VERSION变更
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return instance!!
        }
    }
}