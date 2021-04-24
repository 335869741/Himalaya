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

abstract class BaseUILoaderFragment<B: ViewBinding, V: ViewModel> : BaseFragment<B, V>() {

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