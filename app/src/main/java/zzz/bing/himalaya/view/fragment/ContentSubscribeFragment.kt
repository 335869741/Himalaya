package zzz.bing.himalaya.view.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentContentSubscribeBinding
import zzz.bing.himalaya.db.entity.AlbumSubscribe
import zzz.bing.himalaya.view.adapter.ContentSubscribeAdapter
import zzz.bing.himalaya.viewmodel.ContentSubscribeViewModel
import zzz.bing.himalaya.views.DialogSubscribe

class ContentSubscribeFragment :
    BaseFragment<FragmentContentSubscribeBinding, ContentSubscribeViewModel>() {

    private lateinit var mSubscribeAdapter: ContentSubscribeAdapter

    override fun initViewModel() =
        ViewModelProvider(this).get(ContentSubscribeViewModel::class.java)

    override fun initViewBinding() = FragmentContentSubscribeBinding.inflate(layoutInflater)

    override fun initView() {
        mSubscribeAdapter = ContentSubscribeAdapter()
        binding.recycler.adapter = mSubscribeAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun initListener() {
        mSubscribeAdapter.setClickEvent {
            it?.also { album ->
                itemClick(album)
            }
        }
        mSubscribeAdapter.setLongPressEvent {
            it?.also { album ->
                itemOnLongTouCh(album)
            }
        }
        mSubscribeAdapter.addLoadStateListener { loadState ->
            viewIsEmpty(loadState)
        }
    }

    /**
     * 判断是否有数据
     * @param loadState CombinedLoadStates
     */
    private fun viewIsEmpty(loadState: CombinedLoadStates) {
        if (loadState.source.refresh is LoadState.NotLoading
            && loadState.append.endOfPaginationReached
            && mSubscribeAdapter.itemCount < 1
        ) {
            binding.recycler.postDelayed(
                {
                    if (mSubscribeAdapter.itemCount < 1) {
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

    override fun initObserver() {

    }

    override fun initData() {
        getData()
    }

    override fun onResume() {
        mSubscribeAdapter.refresh()
        super.onResume()
    }

    /**
     * 长按事件
     */
    private fun itemOnLongTouCh(item: AlbumSubscribe) {
        DialogSubscribe(requireContext())
            .setSubmitClickListener {
                viewModel.removeAlbum(item)
                binding.root.postDelayed({
                    mSubscribeAdapter.refresh()
                    Snackbar.make(binding.root, "删除了一个订阅的专辑", Snackbar.LENGTH_LONG)
                        .setAction("撤销") {
                            viewModel.addSubscribe(item)
                            binding.root.post { mSubscribeAdapter.refresh() }
                        }.show()
                }, 150)
                it.dismiss()
            }.setCancelClickListener {
                it.dismiss()
            }.show()
    }

    /**
     * 点击事件
     */
    private fun itemClick(item: AlbumSubscribe) {
        findNavController().navigate(R.id.action_homeFragment_to_detailFragment,
            Bundle().apply { putParcelable(AlbumDetailFragment.ACTION_ALBUM, item) }
        )
    }

    /**
     * 获得数据
     */
    private fun getData() {
        lifecycleScope.launch {
            viewModel.getAlbum().collect { pagingData ->
                mSubscribeAdapter.submitData(pagingData)
            }
        }
    }
}