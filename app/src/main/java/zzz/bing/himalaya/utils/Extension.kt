package zzz.bing.himalaya.utils

import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.track.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            LogUtils.w(this, "ImageUrl is null")
            null
        }
    }
}

/**
 *
 * @return String?
 */
fun Track.getImageUrl(): String? {
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
            LogUtils.w(this, "ImageUrl is null")
            null
        }
    }
}

/**
 * 简化添加操作
 * 先clear后addAll
 * @receiver ArrayList<T>
 * @param list List<T>
 */
fun <T> ArrayList<T>.putAll(list: List<T>) {
    clear()
    addAll(list)
}

/**
 * 格式化时间
 * @return String 300秒转化为 05：00
 */
fun Int.timeUtil(): String {
    return when {
        this > 59 -> {
            val min = if (this / 60 > 9) {
                "${this / 60}"
            } else {
                "0${this / 60}"
            }
            val s = if (this % 60 > 9) {
                "${this % 60}"
            } else {
                "0${this % 60}"
            }
            "$min:$s"
        }
        this > 9 -> {
            "00:${this}"
        }
        else -> {
            "00:0${this}"
        }
    }
}

/**
 * 搜索track所在的位置
 * @receiver List<Track>
 * @param dataId Long
 * @return Int 未搜索到时返回 -1
 */
fun List<Track>.trackSearch(dataId: Long): Int {
    for ((index, item) in withIndex()) {
        if (item.dataId == dataId) {
            return index
        }
    }
    return -1
}

/**
 * 不可变序列不能修改内容，通过返回新对象的方式修改
 * @receiver List<T>
 * @return ArrayList<T>
 */
fun <T> List<T>.onReverse(): ArrayList<T> {
    val array = ArrayList<T>()
    array.addAll(this)
    array.reverse()
    return array
}

/**
 * 数据库操作需要多线程
 * 使用高阶函数简化操作
 * @param block Function0<Unit>
 */
fun fire(block: suspend () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        block()
    }
}