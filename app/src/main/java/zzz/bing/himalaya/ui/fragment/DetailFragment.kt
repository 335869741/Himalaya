package zzz.bing.himalaya.ui.fragment

import android.graphics.Color
import android.transition.TransitionInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentDetailBinding
import zzz.bing.himalaya.ui.MainActivity
import zzz.bing.himalaya.ui.adapter.DetailAdapter
import zzz.bing.himalaya.utils.Base64Util
import zzz.bing.himalaya.utils.ImageBlur
import zzz.bing.himalaya.viewmodel.DetailViewModel

class DetailFragment : BaseFragment<FragmentDetailBinding, DetailViewModel>() {

    companion object {
        const val ACTION_COVER_IMAGE_BASE64 = "action_cover_image_base64"
        const val ACTION_ALBUM_TITLE = "action_album_title"
        const val ACTION_AUTHOR = "action_author"
    }

    //是否展开
    private var isExpanded = true
    private lateinit var mDetailAdapter :DetailAdapter

    override fun initViewModel() = ViewModelProvider(this).get(DetailViewModel::class.java)

    override fun initViewBinding() = FragmentDetailBinding.inflate(layoutInflater)

    override fun initView() {
        setBarColor()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        mDetailAdapter = DetailAdapter()
        binding.recycler.adapter = mDetailAdapter
    }

    override fun initData() {
        arguments?.also {
            binding.collapsingToolbar.title =
                it.getString(ACTION_ALBUM_TITLE, requireContext().getString(R.string.app_name))
            binding.textAuthor.text =
                it.getString(ACTION_AUTHOR, requireContext().getString(R.string.app_name))
            it.getString(ACTION_COVER_IMAGE_BASE64)?.also { base64 ->
                binding.imageItemIcon.setImageBitmap(
                    Base64Util.base64ToBitmap(base64)
                )
                binding.imageBackground.setImageBitmap(
                    ImageBlur.renderScriptBlur(
                        requireContext(),
                        Base64Util.base64ToBitmap(base64)!!,
                        1,
                        0.1f
                    )
                )
            }
            sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.transition_element)
        }
    }

    override fun initListener() {
        binding.toolbar.setOnClickListener {
            if (!isExpanded) {
                binding.appbar.setExpanded(true)
            }
        }
        binding.appbar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                if (verticalOffset != 0){
                    isExpanded = false
                }
            })
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

    override fun onDestroyView() {
        super.onDestroyView()
        setBarColor()
    }
}