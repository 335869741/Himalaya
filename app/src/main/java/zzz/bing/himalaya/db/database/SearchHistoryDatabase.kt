package zzz.bing.himalaya.db.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import zzz.bing.himalaya.BaseApplication
import zzz.bing.himalaya.db.dao.SearchHistoryDao
import zzz.bing.himalaya.db.entity.SearchHistory

@Database(
    entities = [SearchHistory::class],
    version = SearchHistoryDatabase.SEARCH_HISTORY_DATABASE_VERSION,
    exportSchema = true
)
abstract class SearchHistoryDatabase : RoomDatabase() {
    abstract fun userSearchHistoryDao(): SearchHistoryDao

    companion object {
        private var instance: SearchHistoryDatabase? = null

        const val SEARCH_HISTORY_DATABASE_VERSION = 1

        fun getSearchHistoryDatabase(): SearchHistoryDatabase {
            if (instance == null) {
                synchronized(SearchHistoryDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            BaseApplication.getContext().applicationContext,
                            SearchHistoryDatabase::class.java,
                            "user_search_history_database.db"
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