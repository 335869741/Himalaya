package zzz.bing.himalaya.view.fragment

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
    }

    override fun initData() {
        lifecycleScope.launch {
            viewModel.getAlbum().collect { pagingData ->
                mHistoryAdapter.submitData(pagingData)
            }
        }
    }
}