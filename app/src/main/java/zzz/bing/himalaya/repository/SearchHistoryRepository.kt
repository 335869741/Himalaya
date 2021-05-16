package zzz.bing.himalaya.repository

import androidx.lifecycle.LiveData
import zzz.bing.himalaya.db.database.SearchHistoryDatabase
import zzz.bing.himalaya.db.entity.SearchHistory
import zzz.bing.himalaya.utils.fire

object SearchHistoryRepository {
    private val mSearchHistoryDao by lazy {
        SearchHistoryDatabase.getSearchHistoryDatabase().userSearchHistoryDao()
    }

    /**
     * 添加
     * @param text String
     * @return Job
     */
    fun addSearchHistory(text: String) = fire {
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
    fun removeAllSearchHistory() = fire {
        mSearchHistoryDao.deleteAllSearchHistory()
    }
}