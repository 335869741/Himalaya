package zzz.bing.himalaya.ui.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ximalaya.ting.android.opensdk.model.album.Album
import zzz.bing.himalaya.BaseUILoaderFragment
import zzz.bing.himalaya.databinding.FragmentContentHomeBinding
import zzz.bing.himalaya.ui.adapter.ContentHomeAdapter
import zzz.bing.himalaya.viewmodel.ContentHomeViewModel
import zzz.bing.himalaya.views.UILoader

class ContentHomeFragment : BaseUILoaderFragment<FragmentContentHomeBinding, ContentHomeViewModel>() {


    private val mContentHomeAdapter by lazy { ContentHomeAdapter(this) }

    override fun initViewModel() = ViewModelProvider(this).get(ContentHomeViewModel::class.java)
    override fun initViewBinding() = FragmentContentHomeBinding.inflate(layoutInflater)
    override fun getUIStatusLiveData() = viewModel.uiStatusLiveData

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
//        binding.constraint.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
//            override fun onGlobalLayout() {
//                val height = binding.constraint.measuredHeight
//                val params = binding.recycler.layoutParams
//                params.height = height
////                binding.recycler.layoutParams = params
//                LogUtils.d(this@ContentHomeFragment,"onGlobalLayout $height")
//                if (height != 0){
//                    binding.constraint.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//            }
//        })
    }

    private fun reLoadData() {
        if (viewModel.uiStatusLiveData.value != UILoader.UIStatus.LOADING &&
                viewModel.uiStatusLiveData.value != UILoader.UIStatus.SUCCESS){
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


}