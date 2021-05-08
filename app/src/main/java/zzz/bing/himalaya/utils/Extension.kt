package zzz.bing.himalaya.utils

import androidx.transition.Transition
import androidx.transition.TransitionSet
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.track.Track
import java.util.*
import kotlin.collections.ArrayList

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
            LogUtils.w(
                this,
                "ImageUrl is null"
            )
            null
        }
    }
}

/**
 * Executes the specified [body] when the request is complete. It is invoked no matter whether the
 * request succeeds or fails.
 * @receiver RequestBuilder<T>
 * @param body Function0<Unit>
 * @return RequestBuilder<T>
 */
fun <T> RequestBuilder<T>.doOnEnd(body: () -> Unit): RequestBuilder<T> {
    return addListener(object : RequestListener<T> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            body()
            return false
        }

        override fun onResourceReady(
            resource: T,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            body()
            return false
        }
    })
}

inline fun transitionTogether(crossinline body: TransitionSet.() -> Unit): TransitionSet {
    return TransitionSet().apply {
        ordering = TransitionSet.ORDERING_TOGETHER
        body()
    }
}

operator fun TransitionSet.plusAssign(transition: Transition) {
    addTransition(transition)
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

fun List<Track>.trackSearch(dataId: Long): Int {
    for ((index, item) in withIndex()) {
        if (item.dataId == dataId){
            return index
        }
    }
    return -1
}