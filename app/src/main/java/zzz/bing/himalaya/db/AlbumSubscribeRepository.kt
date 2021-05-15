package zzz.bing.himalaya.db

import zzz.bing.himalaya.db.database.AlbumSubscribeDatabase
import zzz.bing.himalaya.db.entity.AlbumSubscribe
import zzz.bing.himalaya.utils.fire

@Suppress("MemberVisibilityCanBePrivate")
object AlbumSubscribeRepository {
    private val mAlbumSubscribeDao by lazy {
        AlbumSubscribeDatabase.getSearchHistoryDatabase().userAlbumSubscribeDao()
    }

    var pageSize = 10

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
     * @return List<SearchHistory>
     */
    suspend fun getAlbumSubscribe(page: Int): List<AlbumSubscribe> {
        return getAlbumSubscribe(page, pageSize)
    }

    /**
     * 使用页数和每页大小获得订阅内容
     * @param page Int
     * @param pageSize Int
     * @return List<AlbumSubscribe>
     */
    suspend fun getAlbumSubscribe(page: Int, pageSize: Int): List<AlbumSubscribe> {
        val limit = pageSize
        val offset = (page - 1) * pageSize
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
     * @return List<AlbumSubscribe>
     */
    suspend fun getSubscribe(subscribe: AlbumSubscribe): List<AlbumSubscribe> {
        return mAlbumSubscribeDao.findAlbumSubscribeById(subscribe.id)
    }
}