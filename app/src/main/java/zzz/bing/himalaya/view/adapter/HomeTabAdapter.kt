package zzz.bing.himalaya.view.adapter

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import zzz.bing.himalaya.view.fragment.ContentHistoryFragment
import zzz.bing.himalaya.view.fragment.ContentHomeFragment
import zzz.bing.himalaya.view.fragment.ContentSubscribeFragment

class HomeTabAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    

    override fun getItemCount() = 3

    override fun createFragment(position: Int) =
        when(position){
            0 -> ContentHomeFragment()
            1 -> ContentSubscribeFragment()
            else -> ContentHistoryFragment()
        }
}