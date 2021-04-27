package zzz.bing.himalaya.view.fragment

import androidx.lifecycle.ViewModelProvider
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.databinding.FragmentContentHistoryBinding
import zzz.bing.himalaya.viewmodel.ContentHistoryViewModel

class ContentHistoryFragment : BaseFragment<FragmentContentHistoryBinding, ContentHistoryViewModel>() {
    override fun initViewModel() = ViewModelProvider(this).get(ContentHistoryViewModel::class.java)

    override fun initViewBinding() = FragmentContentHistoryBinding.inflate(layoutInflater)
}