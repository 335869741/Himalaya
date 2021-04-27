package zzz.bing.himalaya.view.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ximalaya.ting.android.opensdk.model.album.Album
import zzz.bing.himalaya.BaseUILoaderFragment
import zzz.bing.himalaya.databinding.FragmentContentHomeBinding
import zzz.bing.himalaya.view.adapter.ContentHomeAdapter
import zzz.bing.himalaya.viewmodel.ContentHomeViewModel
import zzz.bing.himalaya.views.UILoader

class ContentHomeFragment : BaseUILoaderFragment<FragmentContentHomeBinding, ContentHomeViewModel>() {

    private val mContentHomeAdapter by lazy { ContentHomeAdapter(this) }

    override fun initViewModel() = ViewModelProvider(this).get(ContentHomeViewModel::class.java)
    override fun initViewBinding() = FragmentContentHomeBinding.inflate(layoutInflater)
    override fun getUIStatusLiveData() = viewModel.uiStatus

    override fun initView() {
        binding.recycler.adapter = mContentHomeAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.isNestedScrollingEnabled = false
    }

    override fun initData() {
        viewModel.getRecommendAlbum()
    }

    override fun initObserver() {
        viewModel.albumList.observe(this, { albumList ->
            if (albumList != null){
                albumListOnChange(albumList)
            }
        })
    }

    override fun initListener() {
        uiLoader.networkError.setOnClickListener {
            reLoadData()
        }
        uiLoader.empty.setOnClickListener {
            reLoadData()
        }
    }

    /**
     * 重新加载数据
     */
    private fun reLoadData() {
        if (viewModel.uiStatus.value != UILoader.UIStatus.LOADING &&
                viewModel.uiStatus.value != UILoader.UIStatus.SUCCESS){
            viewModel.getRecommendAlbum()
        }
    }

    /**
     * 提交数据
     * @param albumList List<Album>
     */
    private fun albumListOnChange(albumList: List<Album>) {
        mContentHomeAdapter.submitList(albumList)
    }

    fun onChangItemForId(id:Int){
        mContentHomeAdapter.notifyItemChanged(id)
    }
}
