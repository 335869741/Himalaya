package zzz.bing.himalaya.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.ChangeTransform
import androidx.transition.Transition
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.ximalaya.ting.android.opensdk.model.track.Track
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentAlbumDetailBinding
import zzz.bing.himalaya.ui.MainActivity
import zzz.bing.himalaya.ui.adapter.AlbumDetailAdapter
import zzz.bing.himalaya.utils.*
import zzz.bing.himalaya.viewmodel.AlbumDetailViewModel
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
    private lateinit var mAlbumDetailAdapter: AlbumDetailAdapter
    private val mItemId: Long by lazy { arguments?.getLong(ACTION_ITEM_ID)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // These are the shared element transitions.
        sharedElementEnterTransition = createSharedElementTransition(LARGE_EXPAND_DURATION)
        sharedElementReturnTransition = createSharedElementTransition(LARGE_COLLAPSE_DURATION)
    }

    override fun initViewModel() = ViewModelProvider(this).get(AlbumDetailViewModel::class.java)

    override fun initViewBinding() = FragmentAlbumDetailBinding.inflate(layoutInflater)

    override fun initView() {
        setBarColor()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        mAlbumDetailAdapter = AlbumDetailAdapter()
        binding.recycler.adapter = mAlbumDetailAdapter
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
//                glide.fallbackDrawable
                glide.into(binding.imageBackground)
            }
        }
        viewModel.getTracksOrNull(mItemId)
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
            LogUtils.d(this,"textOnPlay")
        }
        binding.textOnSelect.setOnClickListener {
            LogUtils.d(this,"textOnSelect")
        }
        binding.textSubscribe.setOnClickListener {
            LogUtils.d(this,"textSubscribe")
        }
    }

    override fun initObserver() {
        viewModel.trackLiveData.observe(this){ tracks ->
            if (!tracks.isNullOrEmpty()){
                setTrackS(tracks)
            }
        }
    }

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
}