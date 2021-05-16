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
        binding.root.adapter = mSubscribeAdapter
        binding.root.layoutManager = LinearLayoutManager(requireContext())
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
    }

    override fun initObserver() {

    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    /**
     * 长按事件
     */
    private fun itemOnLongTouCh(item: AlbumSubscribe) {
        DialogSubscribe(requireContext())
            .setSubmitClickListener {
                viewModel.removeAlbum(item)
                binding.root.postDelayed({ mSubscribeAdapter.refresh() }, 100)
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

    private fun getData() {
        lifecycleScope.launch {
            viewModel.getAlbum().collect { pagingData ->
                mSubscribeAdapter.submitData(pagingData)
            }
        }
    }
}