package zzz.bing.himalaya.view.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.Transition
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
import zzz.bing.himalaya.repository.PlayerManager
import zzz.bing.himalaya.utils.*
import zzz.bing.himalaya.view.MainActivity
import zzz.bing.himalaya.view.adapter.AlbumDetailAdapter
import zzz.bing.himalaya.viewmodel.AlbumDetailViewModel
import zzz.bing.himalaya.views.UILoader
import java.util.concurrent.TimeUnit

class AlbumDetailFragment : BaseFragment<FragmentAlbumDetailBinding, AlbumDetailViewModel>() {

    companion object {
        const val ACTION_COVER_IMAGE_URL = "action_cover_image_base64"
        const val ACTION_ALBUM_TITLE = "action_album_title"
        const val ACTION_AUTHOR = "action_author"
        const val ACTION_ITEM_ID = "item_id"
        const val TRANSITION_IMAGE_ICON = "Transition_Image_icon"
    }

    //是否展开
    private var isExpanded = true
    private var mPassPlay = false
    private var mIsInit = true

    private lateinit var mAlbumDetailAdapter: AlbumDetailAdapter
    private lateinit var mRecycler: MyRecycler

    private val mItemId: Long by lazy { arguments?.getLong(ACTION_ITEM_ID)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = createSharedElementTransition(LARGE_EXPAND_DURATION)
        sharedElementReturnTransition = createSharedElementTransition(LARGE_COLLAPSE_DURATION)
    }

    override fun initViewModel() = ViewModelProvider(this).get(AlbumDetailViewModel::class.java)

    override fun initViewBinding() = FragmentAlbumDetailBinding.inflate(layoutInflater)

    override fun initView() {
        mRecycler = MyRecycler()
        binding.frame.addView(mRecycler)
        val recycler = mRecycler.recycler
        recycler.layoutManager = LinearLayoutManager(requireContext())
        mAlbumDetailAdapter = AlbumDetailAdapter(this)
        recycler.adapter = mAlbumDetailAdapter
        binding.textOnPlay.isSelected = true
    }

    override fun initData() {
        arguments?.also {
            binding.collapsingToolbar.title =
                it.getString(ACTION_ALBUM_TITLE, requireContext().getString(R.string.app_name))
            binding.textAuthor.text =
                it.getString(ACTION_AUTHOR, requireContext().getString(R.string.app_name))
            it.getString(ACTION_COVER_IMAGE_URL)?.also { url ->
                val glide = Glide.with(this).load(url).dontTransform()
                    .doOnEnd(::startPostponedEnterTransition)
                glide.into(binding.imageAlbumIcon)
                glide.into(binding.imageBackground)
            }
        }
        viewModel.getTracks(mItemId)
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
        binding.textOnSelect.setOnClickListener { view ->
            onSelectListClick(view)
        }
        binding.textSubscribe.setOnClickListener { view ->
            onSubscribeClick(view)
        }
    }

    /**
     * 订阅事件
     * @param view View
     */
    private fun onSubscribeClick(view: View) {
        LogUtils.d(this, "onSubscribeClick")
    }

    /**
     * 选集事件
     * @param view View
     */
    private fun onSelectListClick(view: View) {
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
     *
     * @param playerState PlayerState
     */
    private fun playerStateChange(playerState: PlayerManager.PlayerState) {
        when (playerState) {
            PlayerManager.PlayerState.Playing -> {
                val playIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_pause_black)
                binding.textOnPlay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    playIcon,
                    null,
                    null,
                    null
                )
                binding.textOnPlay.text = (viewModel.voice as Track).trackTitle
            }
            PlayerManager.PlayerState.Stopped -> {
                val playIcon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_play_black)
                binding.textOnPlay.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    playIcon,
                    null,
                    null,
                    null
                )
                binding.textOnPlay.text = "继续播放"
                viewModel.trackLiveData.value.also { tracks ->
                    if (!tracks.isNullOrEmpty()) {
                        if (tracks.trackSearch(viewModel.voice.dataId) == -1) {
                            binding.textOnPlay.text = "播放全部"
                        }
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
                    playIcon,
                    null,
                    null,
                    null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // We are expecting an enter transition from the grid fragment.
        postponeEnterTransition(500L, TimeUnit.MILLISECONDS)

        // Transition names. Note that they don't need to match with the names of the selected grid
        // item. They only have to be unique in this fragment.
        ViewCompat.setTransitionName(binding.imageAlbumIcon, "${TRANSITION_IMAGE_ICON}_${mItemId}")
    }

    override fun onResume() {
        super.onResume()
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

    override fun onStop() {
        super.onStop()
        setBarColor()
    }

    /**
     * 创建共享元素的动画
     * @param duration Long
     * @return Transition
     */
    private fun createSharedElementTransition(duration: Long): Transition {
        return transitionTogether {
            this.duration = duration
            interpolator = FAST_OUT_SLOW_IN
            this += SharedFade()
            this += ChangeImageTransform()
            this += ChangeBounds()
            this += ChangeTransform()
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
     *
     */
    private fun loadMoreListener() {
        viewModel.getTracks(mItemId)
    }

    inner class MyRecycler : UILoader(requireContext()) {
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
                this@AlbumDetailFragment.loadMoreListener()
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