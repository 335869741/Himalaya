package zzz.bing.himalaya.ui.fragment

import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentDetailBinding
import zzz.bing.himalaya.ui.MainActivity
import zzz.bing.himalaya.viewmodel.DetailViewModel

class DetailFragment : BaseFragment<FragmentDetailBinding,DetailViewModel>() {
    override fun initViewModel() = ViewModelProvider(this).get(DetailViewModel::class.java)

    override fun initViewBinding() = FragmentDetailBinding.inflate(layoutInflater)

    override fun initView() {
        setWindow()
    }

    private fun setWindow() {
        val window = (requireActivity() as MainActivity).window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.main_transparent)
//        //因为android 11 中systemUiVisibility设置已弃用，所以判断版本再用相应方法
//        //但是因为没有11的设备，所以setDecorFitsSystemWindows false的效果还不清楚
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.setDecorFitsSystemWindows(false)
//        } else {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            window.statusBarColor = Color.TRANSPARENT
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val window = (requireActivity() as MainActivity).window
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.main)
    }
}