package zzz.bing.himalaya.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import zzz.bing.himalaya.db.entity.AlbumSubscribe

@Dao
interface AlbumSubscribeDao {

    @Insert
    suspend fun insertAlbumSubscribe(vararg albumSubscribe: AlbumSubscribe)

    @Query("DELETE FROM album_subscribe")
    suspend fun deleteAllAlbumSubscribe()

    @Query("SELECT * FROM album_subscribe where id = :id")
    fun findAlbumSubscribeById(id: Long): List<AlbumSubscribe>

    @Query("SELECT * FROM album_subscribe where id = :id")
    fun findAlbumSubscribeByIdWithLiveData(id: Long): LiveData<AlbumSubscribe>

    @Delete
    suspend fun deleteAlbumSubscribe(vararg searchHistory: AlbumSubscribe)

    @Query("SELECT * FROM album_subscribe ORDER BY id DESC LIMIT :mLimit OFFSET :mOffset")
    fun findAlbumSubscribeByLimitAndOffset(
        mLimit: Int,
        mOffset: Int
    ): LiveData<List<AlbumSubscribe>>
}