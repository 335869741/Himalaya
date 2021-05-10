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
import zzz.bing.himalaya.utils.LogUtils

abstract class UILoader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    enum class UIStatus {
        LOADING, SUCCESS, NETWORK_ERROR, EMPTY, NONE, LOAD_MORE
    }

    private var beforeNetStatus: UIStatus? = null

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
            LogUtils.i(this, "uiStatus ==> $uiStatus")
            if (beforeNetStatus == UIStatus.LOAD_MORE) {
                if (uiStatus == UIStatus.NETWORK_ERROR) {
                    loadMoreError()
                }
                if (uiStatus == UIStatus.EMPTY) {
                    loadMoreEmpty()
                }
            } else {
                mNetworkError.root.visibility =
                    if (uiStatus == UIStatus.NETWORK_ERROR) VISIBLE else GONE
                mEmpty.root.visibility =
                    if (uiStatus == UIStatus.EMPTY) VISIBLE else GONE
                mLoading.root.visibility =
                    if (uiStatus == UIStatus.LOADING) VISIBLE else GONE
                mSuccess.visibility =
                    if (uiStatus == UIStatus.SUCCESS
                        || uiStatus == UIStatus.LOAD_MORE
                    ) VISIBLE else GONE
            }
            uiStatusChange(uiStatus)
            beforeNetStatus = uiStatus
        }
    }

    /**
     * 加载更多返回空
     * 应该没有更多了
     */
    open fun loadMoreEmpty() {}

    /**
     * 加载更多的失败
     */
    open fun loadMoreError() {}

    /**
     * 子类需要监听ui状态时重写此方法
     * @param uiStatus UIStatus
     */
    open fun uiStatusChange(uiStatus: UIStatus) {}

    /**
     * 获得成功时的ui
     * @return view
     */
    abstract fun getSuccessView(): View

    /**
     * 获得状态的liveData
     * @return LiveData<UIStatus>
     */
    abstract fun getUIStatusLiveData(): LiveData<UIStatus>

    /**
     * 获得activity或fragment的生命周期
     * @return LifecycleOwner
     */
    abstract fun getLifecycleOwner(): LifecycleOwner
}
