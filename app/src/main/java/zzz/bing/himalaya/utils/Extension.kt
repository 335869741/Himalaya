package zzz.bing.himalaya.utils

import com.ximalaya.ting.android.opensdk.model.album.Album

object Extension {

    /**
     *
     * @return String?
     */
    fun Album.getImageUrl(): String? {
        return when {
            !coverUrlMiddle.isNullOrEmpty() -> {
                coverUrlMiddle
            }
            !coverUrlMiddle.isNullOrEmpty() -> {
                coverUrlMiddle
            }
            !coverUrlSmall.isNullOrEmpty() -> {
                coverUrlSmall
            }
            else -> {
                LogUtils.w(
                    this,
                    "ImageUrl is null"
                )
                null
            }
        }
    }
}