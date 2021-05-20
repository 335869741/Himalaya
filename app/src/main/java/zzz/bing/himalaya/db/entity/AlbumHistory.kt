package zzz.bing.himalaya.db.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_history")
data class AlbumHistory(
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "info") var info: String,
    @ColumnInfo(name = "cover_url") var coverUrl: String?,
    @ColumnInfo(name = "play_ni_count") var playNiCount: Int,
    @ColumnInfo(name = "album_id") var albumId: Long
) : Parcelable {

    constructor(
        title: String,
        info: String,
        coverUrl: String?,
        playNiCount: Int,
        albumId: Long,
        id: Long
    )
            : this(title, info, coverUrl, playNiCount, albumId) {
        this.id = id
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong()
    ) {
        id = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(info)
        parcel.writeString(coverUrl)
        parcel.writeInt(playNiCount)
        parcel.writeLong(albumId)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumHistory> {
        override fun createFromParcel(parcel: Parcel): AlbumHistory {
            return AlbumHistory(parcel)
        }

        override fun newArray(size: Int): Array<AlbumHistory?> {
            return arrayOfNulls(size)
        }
    }


}
