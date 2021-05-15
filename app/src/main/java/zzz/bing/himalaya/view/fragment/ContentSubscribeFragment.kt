package zzz.bing.himalaya.view.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.databinding.FragmentContentSubscribeBinding
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.view.adapter.ContentSubscribeAdapter
import zzz.bing.himalaya.viewmodel.ContentSubscribeViewModel

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
        lifecycleScope.launch {
            viewModel.getAlbum().collect { pagingData ->
                mSubscribeAdapter.submitData(pagingData)
            }
        }
    }

    override fun initListener() {
        mSubscribeAdapter.setBindingEvent {
            it.root.setOnClickListener {
                itemClick()
            }
            it.root.setOnTouchListener { _, event ->
                if (event.downTime > 1000) {
                    itemOnLongTouCh()
                }
                false
            }
        }
    }

    override fun initObserver() {

    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * 长按事件
     */
    private fun itemOnLongTouCh() {

    }

    /**
     * 点击事件
     */
    private fun itemClick() {
        viewModel.removeAlbum()
        LogUtils.d(this, "click")
    }
}