package zzz.bing.himalaya.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import zzz.bing.himalaya.databinding.LayoutEmptyBinding
import zzz.bing.himalaya.databinding.LayoutErrorBinding
import zzz.bing.himalaya.databinding.LayoutLoadingBinding

abstract class UILoader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    enum class UIStatus {
        LOADING, SUCCESS, NETWORK_ERROR, EMPTY, NONE
    }

    private val mLoading by lazy {
        LayoutLoadingBinding.inflate(LayoutInflater.from(context))
    }
    private val mNetworkError by lazy {
        LayoutErrorBinding.inflate(LayoutInflater.from(context))
    }
    private val mEmpty by lazy {
        LayoutEmptyBinding.inflate(LayoutInflater.from(context))
    }
    private val mSuccess by lazy { getSuccessView() }

    private val mUIStatusLiveData by lazy { getUIStatusLiveData() }

    private val mLifecycleOwner by lazy { getLifecycleOwner() }

    val loading by lazy { mLoading.root }
    val networkError by lazy { mNetworkError.root }
    val empty by lazy { mEmpty.root }
    val success by lazy { mSuccess }


    init {
        loadView()
        switchUIByCurrentStatus()
    }

    /**
     * 加载view
     */
    private fun loadView() {
        addView(mLoading.root.apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
        addView(mNetworkError.root.apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
        addView(mSuccess.apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
        addView(mEmpty.root.apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        })
    }

    /**
     * 观察status变化修改ui
     */
    private fun switchUIByCurrentStatus() {
        mUIStatusLiveData.observe(mLifecycleOwner) { uiStatus ->
            mLoading.root.visibility =
                if (uiStatus == UIStatus.LOADING) VISIBLE else GONE
            mNetworkError.root.visibility =
                if (uiStatus == UIStatus.NETWORK_ERROR) VISIBLE else GONE
            mSuccess.visibility =
                if (uiStatus == UIStatus.SUCCESS) VISIBLE else GONE
            mEmpty.root.visibility =
                if (uiStatus == UIStatus.EMPTY) VISIBLE else GONE
        }
    }

    /**
     * 获得成功时的ui
     * @return T
     */
    abstract fun getSuccessView(): View

    /**
     * 获得状态的liveData
     * @return LiveData<UIStatus>
     */
    abstract fun getUIStatusLiveData(): LiveData<UIStatus>

    /**
     * 获得
     * @return LifecycleOwner
     */
    abstract fun getLifecycleOwner(): LifecycleOwner
}
