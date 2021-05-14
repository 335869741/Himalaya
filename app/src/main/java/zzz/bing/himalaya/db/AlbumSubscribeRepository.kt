package zzz.bing.himalaya.db

import androidx.lifecycle.LiveData
import zzz.bing.himalaya.db.database.AlbumSubscribeDatabase
import zzz.bing.himalaya.db.entity.AlbumSubscribe
import zzz.bing.himalaya.utils.fire

@Suppress("MemberVisibilityCanBePrivate")
object AlbumSubscribeRepository {
    private val mAlbumSubscribeDao by lazy {
        AlbumSubscribeDatabase.getSearchHistoryDatabase().userAlbumSubscribeDao()
    }

    var pageSize = 50

    /**
     * 添加
     * @param albumSubscribe AlbumSubscribe
     * @return Job
     */
    fun addAlbumSubscribe(albumSubscribe: AlbumSubscribe) = fire {
        val album = mAlbumSubscribeDao.findAlbumSubscribeById(albumSubscribe.id)
        if (!album.isNullOrEmpty()) {
            mAlbumSubscribeDao.deleteAlbumSubscribe(* album.toTypedArray())
        }
        mAlbumSubscribeDao.insertAlbumSubscribe(albumSubscribe)
    }

    /**
     * 使用页数获得订阅内容
     * liveData不用挂起，room自动优化
     * @return LiveData<List<SearchHistory>>
     */
    fun getAlbumSubscribe(page: Int): LiveData<List<AlbumSubscribe>> {
        val limit = pageSize
        val offset = page - 1 * pageSize
        return getAlbumSubscribe(limit, offset)
    }

    /**
     * 使用页数和每页大小获得订阅内容
     * liveData不用挂起，room自动优化
     * @param page Int
     * @param pageSize Int
     * @return LiveData<List<AlbumSubscribe>>
     */
    fun getAlbumSubscribe(page: Int, pageSize: Int): LiveData<List<AlbumSubscribe>> {
        val limit = pageSize
        val offset = page - 1 * pageSize
        return mAlbumSubscribeDao.findAlbumSubscribeByLimitAndOffset(limit, offset)
    }

    /**
     * 删除所有的订阅
     * @return Job
     */
    fun removeAllAlbumSubscribe() = fire {
        mAlbumSubscribeDao.deleteAllAlbumSubscribe()
    }

    /**
     * 删除特定的订阅
     * @param albumSubscribe AlbumSubscribe
     */
    fun removeAlbumSubscribe(albumSubscribe: AlbumSubscribe) = fire {
        mAlbumSubscribeDao.deleteAlbumSubscribe(albumSubscribe)
    }

    /**
     * 查询订阅列表，用于判断是否已订阅
     * @param subscribe AlbumSubscribe
     * @return LiveData<AlbumSubscribe>
     */
    fun getSubscribe(subscribe: AlbumSubscribe): LiveData<AlbumSubscribe> {
        return mAlbumSubscribeDao.findAlbumSubscribeByIdWithLiveData(subscribe.id)
    }
}