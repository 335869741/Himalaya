package zzz.bing.himalaya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import zzz.bing.himalaya.views.UILoader

abstract class BaseUILoaderFragment<B: ViewBinding, V: ViewModel> : Fragment() {
    protected lateinit var binding: B
    protected lateinit var viewModel: V
    protected val uiLoader by lazy {
        Loader()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = initViewBinding()
        viewModel = initViewModel()
        initView()
        initData()
        initListener()
        initObserver()
        return uiLoader
    }

    /**
     * 初始化ui
     */
    protected open fun initView() {}

    /**
     * 初始化ui事件监听器
     */
    protected open fun initListener() {}

    /**
     *  初始化vm数据观察
     */
    protected open fun initObserver() {}

    /**
     *  初始化ui数据
     */
    protected open fun initData() {}

    /**
     * 获得ViewModel
     * @return V
     */
    abstract fun initViewModel(): V

    /**
     * 获得ViewBinding
     * @return B
     */
    abstract fun initViewBinding(): B

    /**
     * 获得UIStatus的LiveData
     * @return LiveData<UILoader.UIStatus>
     */
    abstract fun getUIStatusLiveData(): LiveData<UILoader.UIStatus>

    inner class Loader : UILoader<B>(requireContext()){
        override fun getSuccessViewBinding() = binding

        override fun getUIStatusLiveData() = this@BaseUILoaderFragment.getUIStatusLiveData()

        override fun getLifecycleOwner() = this@BaseUILoaderFragment
    }
}