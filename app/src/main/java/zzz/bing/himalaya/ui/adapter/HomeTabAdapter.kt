package zzz.bing.himalaya.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import zzz.bing.himalaya.ui.fragment.ContentHistoryFragment
import zzz.bing.himalaya.ui.fragment.ContentHomeFragment
import zzz.bing.himalaya.ui.fragment.ContentSubscribeFragment
import zzz.bing.himalaya.ui.fragment.HomeFragment

class HomeTabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    

    override fun getItemCount() = 3

    override fun createFragment(position: Int) =
        when(position){
            0 -> ContentHomeFragment()
            1 -> ContentSubscribeFragment()
            else -> ContentHistoryFragment()
        }
}