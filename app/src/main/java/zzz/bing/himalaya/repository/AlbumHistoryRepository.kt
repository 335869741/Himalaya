package zzz.bing.himalaya.repository

import zzz.bing.himalaya.db.database.AlbumHistoryDatabase
import zzz.bing.himalaya.db.entity.AlbumHistory
import zzz.bing.himalaya.utils.fire

@Suppress("MemberVisibilityCanBePrivate")
object AlbumHistoryRepository {

    private val mAlbumHistoryDao by lazy {
        AlbumHistoryDatabase.getAlbumHistoryDatabase().userAlbumHistoryDao()
    }

    var pageSize = 10

    /**
     * 添加，如果已存在就删除后再添加，会置顶
     * @param albumHistory AlbumHistory
     * @return Job
     */
    fun addAlbumHistory(albumHistory: AlbumHistory) = fire {
        val album = mAlbumHistoryDao.findAlbumHistoryById(albumId = albumHistory.albumId)
        if (!album.isNullOrEmpty()) {
            mAlbumHistoryDao.deleteAlbumHistory(* album.toTypedArray())
        }
        mAlbumHistoryDao.insertAlbumHistory(albumHistory)
    }

    /**
     * 修改，如果没有就直接添加
     * @param albumHistory AlbumHistory
     */
    fun changeAlbumHistory(albumHistory: AlbumHistory) = fire {
        val album = mAlbumHistoryDao.findAlbumHistoryById(albumHistory.albumId)
        if (album.isNullOrEmpty()) {
            mAlbumHistoryDao.insertAlbumHistory(albumHistory)
        } else {
            mAlbumHistoryDao.updateAlbumHistory(albumHistory)
        }
    }

    /**
     * 删除，直接操作
     * @param albumHistory AlbumHistory
     */
    fun deleteAlbumHistory(albumHistory: AlbumHistory) = fire {
        mAlbumHistoryDao.deleteAlbumHistory(albumHistory)
    }

    /**
     * 使用页数获得播放历史
     * @param page Int
     * @return List<AlbumHistory>
     */
    suspend fun getAlbumHistory(page: Int): List<AlbumHistory> {
        return getAlbumHistory(page, pageSize)
    }

    /**
     * 使用页数和每页大小获得播放历史
     * @param page Int
     * @param pageSize Int
     * @return List<AlbumHistory>
     */
    suspend fun getAlbumHistory(page: Int, pageSize: Int): List<AlbumHistory> {
        val limit = pageSize
        val offset = (page - 1) * pageSize
        return mAlbumHistoryDao.findAlbumHistoryByLimitAndOffset(limit, offset)
    }
}