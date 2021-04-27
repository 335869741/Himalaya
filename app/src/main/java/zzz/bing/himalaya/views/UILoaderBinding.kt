package zzz.bing.himalaya.views

import android.content.Context
import android.util.AttributeSet
import androidx.viewbinding.ViewBinding

abstract class UILoaderBinding<T : ViewBinding> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : UILoader(context, attrs, defStyleAttr) {

    override fun getSuccessView() = getSuccessViewBinding().root

    /**
     * 获得成功时的ui binding
     * @return T
     */
    abstract fun getSuccessViewBinding(): T
}
