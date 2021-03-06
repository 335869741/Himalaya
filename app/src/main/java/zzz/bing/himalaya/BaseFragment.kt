package zzz.bing.himalaya

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<B: ViewBinding, V: ViewModel> : Fragment() {
    protected lateinit var binding: B
    protected lateinit var viewModel: V

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = initViewBinding()
        viewModel = initViewModel()
        initView()
        initData()
        initListener()
        initObserver()
        return binding.root
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

//    /**
//     * 返回事件
//     */
//    open fun onBackPressed(){
//
//    }
}