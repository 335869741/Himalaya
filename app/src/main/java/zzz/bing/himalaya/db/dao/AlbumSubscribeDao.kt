package zzz.bing.himalaya.db.dao

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
    suspend fun findAlbumSubscribeById(id: Long): List<AlbumSubscribe>

    @Delete
    suspend fun deleteAlbumSubscribe(vararg searchHistory: AlbumSubscribe)

    @Query("SELECT * FROM album_subscribe ORDER BY id DESC LIMIT :mLimit OFFSET :mOffset")
    suspend fun findAlbumSubscribeByLimitAndOffset(
        mLimit: Int,
        mOffset: Int
    ): List<AlbumSubscribe>
}