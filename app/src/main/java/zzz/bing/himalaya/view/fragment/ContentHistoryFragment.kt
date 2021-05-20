package zzz.bing.himalaya.view.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.databinding.FragmentContentHistoryBinding
import zzz.bing.himalaya.view.adapter.ContentHistoryAdapter
import zzz.bing.himalaya.viewmodel.ContentHistoryViewModel

class ContentHistoryFragment : BaseFragment<FragmentContentHistoryBinding, ContentHistoryViewModel>() {
    private lateinit var mHistoryAdapter: ContentHistoryAdapter


    override fun initViewModel() = ViewModelProvider(this).get(ContentHistoryViewModel::class.java)

    override fun initViewBinding() = FragmentContentHistoryBinding.inflate(layoutInflater)

    override fun initView() {
        mHistoryAdapter = ContentHistoryAdapter()
        binding.recycler.adapter = mHistoryAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun initListener() {
        mHistoryAdapter.setItemClickEvent {

        }
    }

    override fun initData() {
        lifecycleScope.launch {
            viewModel.getAlbum().collect { pagingData ->
                mHistoryAdapter.submitData(pagingData)
            }
        }
    }
}