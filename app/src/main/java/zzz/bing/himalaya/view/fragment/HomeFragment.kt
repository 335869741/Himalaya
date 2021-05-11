package zzz.bing.himalaya.view.fragment

import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ximalaya.ting.android.opensdk.model.track.Track
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentHomeBinding
import zzz.bing.himalaya.repository.PlayerManager
import zzz.bing.himalaya.view.adapter.HomeTabAdapter
import zzz.bing.himalaya.viewmodel.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    private lateinit var mHomeTabAdapter: HomeTabAdapter

    override fun initViewModel() = ViewModelProvider(this).get(HomeViewModel::class.java)

    override fun initViewBinding() = FragmentHomeBinding.inflate(layoutInflater)

    override fun initView() {
        mHomeTabAdapter = HomeTabAdapter(requireActivity())
        binding.pager.adapter = mHomeTabAdapter
        synchronizationTabAndPage(binding.tab, binding.pager)
    }

    /**
     * 同步tab和pager
     * @param tabLayout TabLayout
     * @param pager ViewPager2
     */
    private fun synchronizationTabAndPage(tabLayout: TabLayout, pager: ViewPager2) {
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            when (position) {
                0 -> tab.text = "推荐"
                1 -> tab.text = "订阅"
                else -> tab.text = "历史"
            }
        }.attach()
    }

    override fun initData() {
        if (viewModel.isPlayIng) {
            binding.playerControl.visibility = View.VISIBLE
        } else {
            binding.playerControl.visibility = View.GONE
        }
    }

    override fun initListener() {
        binding.imagePlay.setOnClickListener {
            playClick()
        }
        binding.playerControl.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_playerFragment)
        }
        binding.imageSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    /**
     * 播放按钮事件
     */
    private fun playClick() {
        if (viewModel.isPlayIng) {
            viewModel.stop()
        } else {
            viewModel.play()
        }
    }

    override fun initObserver() {
        viewModel.playState.observe(viewLifecycleOwner) {
            if (it != null) {
                playStateChange(it)
            }
        }
    }

    /**
     * 播放状态改变
     * @param playerState PlayerState
     */
    private fun playStateChange(playerState: PlayerManager.PlayerState) {
        when (playerState) {
            PlayerManager.PlayerState.Playing -> {
                binding.imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_stop)
                )
                viewModel.nowVoice.also {
                    if (it is Track) {
                        binding.textTitle.text = it.trackTitle
                        binding.textAuthor.text = it.trackIntro
                        Glide.with(this).load(it.coverUrlSmall).into(binding.imageCover)
                    }
                }
            }
            PlayerManager.PlayerState.Stopped -> {
                binding.imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_start)
                )
            }
            PlayerManager.PlayerState.Unusable -> {
                binding.playerControl.visibility = View.GONE
            }
            else -> {

            }
        }
    }
}