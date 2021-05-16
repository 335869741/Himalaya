package zzz.bing.himalaya.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_subscribe")
data class AlbumSubscribe(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "info") var info: String,
    @ColumnInfo(name = "cover_url") var coverUrl: String?,
    @ColumnInfo(name = "track_count") var trackCount: Long,
    @ColumnInfo(name = "play_count") var playCount: Long,
    @ColumnInfo(name = "album_id") var albumId: Long
) : Parcelable {
    constructor(
        title: String,
        info: String,
        coverUrl: String?,
        trackCount: Long,
        playCount: Long,
        albumId: Long,
        id: Long
    )
            : this(title, info, coverUrl, trackCount, playCount, albumId) {
        this.id = id
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong()
    ) {
        id = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(info)
        parcel.writeString(coverUrl)
        parcel.writeLong(trackCount)
        parcel.writeLong(playCount)
        parcel.writeLong(albumId)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumSubscribe> {
        override fun createFromParcel(parcel: Parcel): AlbumSubscribe {
            return AlbumSubscribe(parcel)
        }

        override fun newArray(size: Int): Array<AlbumSubscribe?> {
            return arrayOfNulls(size)
        }
    }
}
