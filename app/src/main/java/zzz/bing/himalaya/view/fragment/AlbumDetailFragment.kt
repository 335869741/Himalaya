package zzz.bing.himalaya.view.fragment

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.ximalaya.ting.android.opensdk.model.track.Track
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentAlbumDetailBinding
import zzz.bing.himalaya.db.entity.AlbumSubscribe
import zzz.bing.himalaya.repository.PlayerManager
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.utils.trackSearch
import zzz.bing.himalaya.view.MainActivity
import zzz.bing.himalaya.view.adapter.AlbumDetailAdapter
import zzz.bing.himalaya.viewmodel.AlbumDetailViewModel
import zzz.bing.himalaya.views.UILoader

class AlbumDetailFragment : BaseFragment<FragmentAlbumDetailBinding, AlbumDetailViewModel>() {

    companion object {
        const val ACTION_ALBUM = "action_album"
    }

    //是否展开
    private var isExpanded = true
    private var mPassPlay = false
    private var mIsInit = true
    private var misSubscribe = false

    private lateinit var mAlbumDetailAdapter: AlbumDetailAdapter
    private lateinit var mMyUILoad: MyUILoad

    private val mAlbumSubscribe by lazy {
        (arguments?.getParcelable(ACTION_ALBUM) as AlbumSubscribe?)!!
    }

    override fun initViewModel() = ViewModelProvider(this).get(AlbumDetailViewModel::class.java)

    override fun initViewBinding() = FragmentAlbumDetailBinding.inflate(layoutInflater)

    override fun initView() {
        mMyUILoad = MyUILoad()
        binding.frame.addView(mMyUILoad)
        val recycler = mMyUILoad.recycler
        recycler.layoutManager = LinearLayoutManager(requireContext())
        mAlbumDetailAdapter = AlbumDetailAdapter(this)
        recycler.adapter = mAlbumDetailAdapter
        binding.textOnPlay.isSelected = true
    }

    override fun initData() {
        binding.collapsingToolbar.title = mAlbumSubscribe.title
        binding.textAuthor.text = mAlbumSubscribe.info
        mAlbumSubscribe.coverUrl?.also { url ->
            val glide = Glide.with(this).load(url).dontTransform()
            glide.into(binding.imageAlbumIcon)
            glide.into(binding.imageBackground)
        }
        viewModel.getTracks(mAlbumSubscribe.albumId)
        LogUtils.d(this, "Album ==> $mAlbumSubscribe")

        getSubscribe()
    }

    override fun initListener() {
        binding.toolbar.setOnClickListener {
            if (!isExpanded) {
                binding.appbar.setExpanded(true)
            }
        }
        binding.appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                if (verticalOffset != 0) {
                    isExpanded = false
                }
            })
        binding.textOnPlay.setOnClickListener {
            onPlayClick()
        }
        binding.textOnSelect.setOnClickListener {
            onSelectListClick()
        }
        binding.textSubscribe.setOnClickListener {
            onSubscribeClick()
        }
    }

    /**
     * 订阅按钮点击事件
     */
    private fun onSubscribeClick() {
        if (misSubscribe) {
            viewModel.removeSubscribe(mAlbumSubscribe)
        } else {
            viewModel.addSubscribe(mAlbumSubscribe)
        }
        binding.textSubscribe.postDelayed({
            getSubscribe()
        }, 100)
    }

    /**
     * 选集事件
     */
    private fun onSelectListClick() {
        LogUtils.d(this, "onSelectListClick")
    }

    /**
     * 播放事件
     */
    private fun onPlayClick() {
        if (viewModel.isPlaying) {
            viewModel.stop()
        } else {
            passPlay()
            putPlayList()
            viewModel.play()
        }
    }

    override fun initObserver() {
        viewModel.trackLiveData.observe(this) { tracks ->
            if (!tracks.isNullOrEmpty()) {
                setTrackS(tracks)
            }
        }
        viewModel.playerState.observe(viewLifecycleOwner) { playerState ->
            playerState?.also {
                playerStateChange(it)
            }
        }
    }

    /**
     * 播放状态改变
     * @param playerState PlayerState
     */
    private fun playerStateChange(playerState: PlayerManager.PlayerState) {
        when (playerState) {
            PlayerManager.PlayerState.Playing -> {
                val playIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_pause_black)
                binding.textOnPlay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    playIcon, null, null, null
                )
                binding.textOnPlay.text = (viewModel.voice as Track).trackTitle
            }
            PlayerManager.PlayerState.Stopped -> {
                val playIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_play_black)
                binding.textOnPlay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    playIcon, null, null, null
                )
                binding.textOnPlay.text = "继续播放"
                viewModel.trackLiveData.value.also { tracks ->
                    if (!tracks.isNullOrEmpty() && tracks.trackSearch(viewModel.voice.dataId) == -1) {
                        binding.textOnPlay.text = "播放全部"
                    }
                }
            }
            PlayerManager.PlayerState.Usable -> {
                if (mPassPlay) {
                    viewModel.play()
                    mPassPlay = false
                    LogUtils.d(this, "Usable mPassPlay == $mPassPlay")
                }
                LogUtils.d(this, "Usable mPassPlay == $mPassPlay")
            }
            else -> {
                val playIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_play_black)
                binding.textOnPlay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    playIcon, null, null, null
                )
                binding.textOnPlay.text = "播放全部"
            }
        }
    }

    /**
     * 提交
     * @param tracks List<Track>
     */
    private fun setTrackS(tracks: List<Track>) {
        mAlbumDetailAdapter.submitList(tracks)
    }

    override fun onResume() {
        super.onResume()
        setBarColor()
    }

    override fun onStop() {
        super.onStop()
        setBarColor()
    }

    /**
     * 设置状态栏透明和恢复状态栏
     */
    private fun setBarColor() {
        val window = (requireActivity() as MainActivity).window
        val color = window.statusBarColor
        window.statusBarColor =
            if (color != ContextCompat.getColor(requireContext(), R.color.main_transparent)) {
                ContextCompat.getColor(requireContext(), R.color.main_transparent)
            } else {
                ContextCompat.getColor(requireContext(), R.color.main)
            }
    }

    /**
     * 提交播放列表同时设定位置
     * @param list List<Track>
     * @param position Int
     */
    fun putPlayList(list: List<Track>, position: Int) {
        viewModel.putPlayList(list, position)
    }

    /**
     * 提交播放列表
     * 已有播放位置时不改变
     * 没有时设为0
     */
    private fun putPlayList() {
        viewModel.onPlayForPlayList()
    }

    /**
     * 初始化
     */
    private fun passPlay() {
        if (mIsInit) {
            mIsInit = false
            mPassPlay = true
        }
    }

    /**
     * 加载更多事件回调
     */
    private fun loadMoreListener() {
        viewModel.getTracks(mAlbumSubscribe.id)
    }

    /**
     * 根据订阅状态改变ui
     */
    private fun getSubscribe() {
        viewModel.getSubscribeAlbum(mAlbumSubscribe) { isSubscribe ->
            binding.textSubscribe.text =
                if (isSubscribe) {
                    misSubscribe = true
                    "已订阅"
                } else {
                    misSubscribe = false
                    "+ 订阅"
                }
        }
    }

    inner class MyUILoad : UILoader(requireContext()) {
        lateinit var recycler: RecyclerView

        private val isLoadMore get() = (success as RefreshLayout).state == RefreshState.Loading

        override fun getSuccessView() = SmartRefreshLayout(context).apply {
            recycler = RecyclerView(context).apply {
                overScrollMode = View.OVER_SCROLL_NEVER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                itemAnimator?.apply {
                    changeDuration = 0
                    moveDuration = 0
                    removeDuration = 0
                }
            }
            setRefreshFooter(ClassicsFooter(context))
            setRefreshContent(recycler)
            setOnLoadMoreListener {
                loadMoreListener()
            }
            setHeaderHeight(0f)
        }

        /**
         *  加载完成
         * @param uiStatus UIStatus
         */
        override fun uiStatusChange(uiStatus: UIStatus) {
            if (isLoadMore && uiStatus == UIStatus.SUCCESS) {
                (success as RefreshLayout).finishLoadMore()
            }
        }

        /**
         * 加载失败，没有更多数据
         */
        override fun loadMoreEmpty() {
            (success as RefreshLayout).finishLoadMoreWithNoMoreData()
        }

        /**
         * 加载失败，网络错误
         */
        override fun loadMoreError() {
            (success as RefreshLayout).finishLoadMore(false)
        }

        override fun getUIStatusLiveData() = viewModel.netState

        override fun getLifecycleOwner() = viewLifecycleOwner
    }
}