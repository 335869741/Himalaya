package zzz.bing.himalaya.db.dao

import androidx.room.*
import zzz.bing.himalaya.db.entity.AlbumHistory

@Dao
interface AlbumHistoryDao {
    @Insert
    suspend fun insertAlbumHistory(vararg albumHistory: AlbumHistory)

    @Query("DELETE FROM album_history")
    suspend fun deleteAllAlbumHistory()

    @Query("SELECT * FROM album_history where album_id = :albumId")
    suspend fun findAlbumHistoryById(albumId: Long): List<AlbumHistory>

    @Delete
    suspend fun deleteAlbumHistory(vararg albumHistory: AlbumHistory)

    @Query("SELECT * FROM album_history ORDER BY id DESC LIMIT :mLimit OFFSET :mOffset")
    suspend fun findAlbumHistoryByLimitAndOffset(
        mLimit: Int,
        mOffset: Int
    ): List<AlbumHistory>

    @Update
    suspend fun updateAlbumHistory(vararg albumHistory: AlbumHistory)
}
