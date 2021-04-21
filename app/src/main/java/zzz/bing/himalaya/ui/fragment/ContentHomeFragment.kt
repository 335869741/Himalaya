package zzz.bing.himalaya.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.album.AlbumList
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.BaseUILoaderFragment
import zzz.bing.himalaya.databinding.FragmentContentHomeBinding
import zzz.bing.himalaya.ui.adapter.ContentHomeAdapter
import zzz.bing.himalaya.viewmodel.ContentHomeViewModel
import zzz.bing.himalaya.views.UILoader

class ContentHomeFragment : BaseUILoaderFragment<FragmentContentHomeBinding, ContentHomeViewModel>() {


    private val mContentHomeAdapter by lazy { ContentHomeAdapter() }

    override fun initViewModel() = ViewModelProvider(this).get(ContentHomeViewModel::class.java)
    override fun initViewBinding() = FragmentContentHomeBinding.inflate(layoutInflater)

    override fun initView() {
        binding.recycler.adapter = mContentHomeAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
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

    override fun getUIStatusLiveData() = viewModel.uiStatusLiveData

    /**
     *
     * @param albumList List<Album>
     */
    private fun albumListOnChange(albumList: List<Album>) {
        mContentHomeAdapter.submitList(albumList)
    }

}