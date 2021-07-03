package zzz.bing.himalaya.view.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentContentHistoryBinding
import zzz.bing.himalaya.db.entity.AlbumSubscribe
import zzz.bing.himalaya.view.adapter.ContentHistoryAdapter
import zzz.bing.himalaya.viewmodel.ContentHistoryViewModel

class ContentHistoryFragment :
    BaseFragment<FragmentContentHistoryBinding, ContentHistoryViewModel>() {
    private lateinit var mHistoryAdapter: ContentHistoryAdapter


    override fun initViewModel() = ViewModelProvider(this).get(ContentHistoryViewModel::class.java)

    override fun initViewBinding() = FragmentContentHistoryBinding.inflate(layoutInflater)

    override fun initView() {
        mHistoryAdapter = ContentHistoryAdapter()
        binding.recycler.adapter = mHistoryAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun initListener() {
        // 点击事件
        mHistoryAdapter.setItemClickEvent {
            val item = it.item!!
            val subscribe = AlbumSubscribe(
                item.title,
                item.info,
                item.coverUrl,
                0L, 0L,
                item.albumId
            )
            findNavController().navigate(
                R.id.action_homeFragment_to_detailFragment,
                Bundle().apply { putParcelable(AlbumDetailFragment.ACTION_ALBUM, subscribe) }
            )
        }
        // 监听适配器状态变化
        mHistoryAdapter.addLoadStateListener { loadState ->
            viewIsEmpty(loadState)
        }
    }

    override fun onResume() {
        mHistoryAdapter.refresh()
        super.onResume()
    }

    /**
     * 判断是否有数据
     * @param loadState CombinedLoadStates
     */
    private fun viewIsEmpty(loadState: CombinedLoadStates) {
        if (loadState.source.refresh is LoadState.NotLoading
            && loadState.append.endOfPaginationReached
            && mHistoryAdapter.itemCount < 1
        ) {
            binding.recycler.postDelayed(
                {
                    if (mHistoryAdapter.itemCount < 1) {
                        binding.recycler.isVisible = false
                        binding.layoutEmpty.root.isVisible = true
                    }
                }, 100
            )
        } else {
            binding.recycler.isVisible = true
            binding.layoutEmpty.root.isVisible = false
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