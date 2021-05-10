package zzz.bing.himalaya.view.fragment

import androidx.lifecycle.ViewModelProvider
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.databinding.FragmentSearchBinding
import zzz.bing.himalaya.viewmodel.SearchViewModel

class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {
    override fun initViewModel() = ViewModelProvider(this).get(SearchViewModel::class.java)

    override fun initViewBinding() = FragmentSearchBinding.inflate(layoutInflater)
}