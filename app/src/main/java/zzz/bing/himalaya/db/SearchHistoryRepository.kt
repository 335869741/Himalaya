package zzz.bing.himalaya.db

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import zzz.bing.himalaya.db.database.SearchHistoryDatabase
import zzz.bing.himalaya.db.entity.SearchHistory

object SearchHistoryRepository {
    private val mSearchHistoryDao by lazy {
        SearchHistoryDatabase.getSearchHistoryDatabase().userSearchHistoryDao()
    }

    /**
     * 添加
     * @param text String
     * @return Job
     */
    fun addSearchHistory(text: String) = set {
        val texts = mSearchHistoryDao.findSearchHistoryBySearchText(text)
        if (!texts.isNullOrEmpty()) {
            mSearchHistoryDao.deleteSearchHistory(* texts.toTypedArray())
        }
        mSearchHistoryDao.insertSearchHistory(SearchHistory(text))
    }

    /**
     * 获取所有历史搜索
     * liveData不用挂起，room自动优化
     * @return LiveData<List<SearchHistory>>
     */
    fun getAllSearchHistory(): LiveData<List<SearchHistory>> {
        return mSearchHistoryDao.findAllSearchHistory()
    }

    /**
     * 删除所有查询的历史数据
     * @return Job
     */
    fun removeAllSearchHistory() = set {
        mSearchHistoryDao.deleteAllSearchHistory()
    }

    /**
     * 数据库操作需要多线程
     * 使用高阶函数简化操作
     * @param block Function0<Unit>
     */
    private fun set(block: suspend () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            block()
        }
    }
}