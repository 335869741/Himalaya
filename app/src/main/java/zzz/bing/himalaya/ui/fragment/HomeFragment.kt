package zzz.bing.himalaya.ui.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.databinding.FragmentHomeBinding
import zzz.bing.himalaya.ui.adapter.HomeTabAdapter
import zzz.bing.himalaya.viewmodel.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    private lateinit var mHomeTabAdapter : HomeTabAdapter
//    private val mHomeTabAdapter by lazy { HomeTabAdapter(requireActivity()) }

    override fun initViewModel() = ViewModelProvider(this).get(HomeViewModel::class.java)

    override fun initViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    override fun initView() {
        mHomeTabAdapter = HomeTabAdapter(requireActivity())
        binding.pager.adapter = mHomeTabAdapter
        synchronizationTabAndPage(binding.tab, binding.pager)
    }

    /**
     * 同步tab和pager
     * @param tabLayout TabLayout
     * @param pager ViewPager2
     */
    private fun synchronizationTabAndPage(tabLayout: TabLayout, pager: ViewPager2) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> tab.text = "推荐"
                1 -> tab.text = "订阅"
                else -> tab.text = "历史"
            }
        }.attach()
    }

    override fun initData() {
    }

    override fun initListener() {
    }

    override fun initObserver() {
    }
}