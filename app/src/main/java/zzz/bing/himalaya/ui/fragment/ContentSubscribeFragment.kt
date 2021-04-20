package zzz.bing.himalaya.ui.fragment

import androidx.lifecycle.ViewModelProvider
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.databinding.FragmentContentSubscribeBinding
import zzz.bing.himalaya.viewmodel.ContentSubscribeViewModel

class ContentSubscribeFragment : BaseFragment<FragmentContentSubscribeBinding, ContentSubscribeViewModel>() {
    override fun initViewModel() = ViewModelProvider(this).get(ContentSubscribeViewModel::class.java)

    override fun initViewBinding() = FragmentContentSubscribeBinding.inflate(layoutInflater)
}