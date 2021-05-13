package zzz.bing.himalaya.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_subscribe")
data class AlbumSubscribe(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "info") var info: String,
    @ColumnInfo(name = "cover_url") var coverUrl: String?
) {
    constructor(title: String, info: String, coverUrl: String?, id: Long)
            : this(title, info, coverUrl) {
        this.id = id
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0
}
