package zzz.bing.himalaya.view.fragment

import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentPlayerBinding
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.view.adapter.PlayerAdapter
import zzz.bing.himalaya.utils.putAll
import zzz.bing.himalaya.utils.timeUtil
import zzz.bing.himalaya.viewmodel.MainViewModel
import zzz.bing.himalaya.viewmodel.PlayerViewModel

class PlayerFragment : BaseFragment<FragmentPlayerBinding, PlayerViewModel>() {

    private lateinit var mPlayerAdapter: PlayerAdapter

    private var mIsUserTouch = false
    private var mInit = false
    private var mProgress = 0

    private val mPlayModeList by lazy {
        listOf(
            ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_sort_descending),
            ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_sort_ascending),
            ContextCompat.getDrawable(requireContext(), R.drawable.selector_play_mode_loop),
            ContextCompat.getDrawable(requireContext(), R.drawable.selector_play_mode_loop_one),
            ContextCompat.getDrawable(requireContext(), R.drawable.selector_play_mode_random)
        )
    }

    override fun initViewModel() = ViewModelProvider(this).get(PlayerViewModel::class.java)

    override fun initViewBinding() = FragmentPlayerBinding.inflate(layoutInflater)

    override fun initView() {
        mPlayerAdapter = PlayerAdapter(requireActivity())
        binding.pager.adapter = mPlayerAdapter

    }

    override fun initObserver() {
        mainViewModel.playList.observe(viewLifecycleOwner) { trackList ->
            mPlayerAdapter.playList.putAll(trackList)
        }
        mainViewModel.playPosition.observe(viewLifecycleOwner) { position ->
            binding.pager.setCurrentItem(position, false)
        }
        mainViewModel.playerState.observe(viewLifecycleOwner) { playerState ->
            if (playerState != null) {
                playerStateChange(playerState)
            }
        }
        mainViewModel.playerBuffer.observe(viewLifecycleOwner) {
            val buffer = if (it == null || it < 0) 0 else if (it > 100) 100 else it
            seekBarBuffer(buffer)
        }
        mainViewModel.playerNow.observe(viewLifecycleOwner) {
            val buffer = if (it == null || it < 0) 0 else it
            seekBarProgress(buffer)
        }
        mainViewModel.playerDuration.observe(viewLifecycleOwner) {
            val duration = if (it == null || it < 0) 0 else it
            timeDuration(duration)
        }
    }

    /**
     * 时长
     * @param duration Int
     */
    private fun timeDuration(duration: Int) {
        binding.textTotalTime.text = (duration / 1000).timeUtil()
    }

    /**
     * 设置当前进度
     * @param buffer Int
     */
    private fun seekBarProgress(buffer: Int) {
        if (!mIsUserTouch) {
            binding.seekBarTime.progress = buffer / 1000
            LogUtils.d(this, "progress ==> $buffer")
        }
        binding.textAfterTime.text = (buffer / 1000).timeUtil()
    }

    /**
     * 设置缓冲进度
     * @param buffer Int
     */
    private fun seekBarBuffer(buffer: Int) {
        val max = binding.seekBarTime.max
        binding.seekBarTime.secondaryProgress = ((buffer * 0.01) * max).toInt()
    }

    /**
     * 播放状态改变
     * @param playerState PlayerState
     */
    private fun playerStateChange(playerState: MainViewModel.PlayerState) {
        when (playerState) {
            MainViewModel.PlayerState.Playing -> {
                binding.imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_stop)
                )
            }
            MainViewModel.PlayerState.Stopped -> {
                binding.imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_start)
                )
            }
            MainViewModel.PlayerState.Usable -> {
                initPlay { mainViewModel.play() }
            }
            else -> {
                binding.imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_start)
                )
            }
        }
    }

    override fun initListener() {
        binding.imagePlay.setOnClickListener { view ->
            playClick(view)
        }
        binding.imagePlayerList.setOnClickListener { view ->
            playListClick(view)
        }
        binding.imagePlayerNext.setOnClickListener { view ->
            playNextClick(view)
        }
        binding.imagePlayerPrevious.setOnClickListener { view ->
            playPreviousClick(view)
        }
        binding.imagePlayerSort.setOnClickListener { view ->
            playSortClick(view)
        }
        binding.seekBarTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mProgress = progress
                    LogUtils.d(this@PlayerFragment, "onProgressChanged")
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mIsUserTouch = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mIsUserTouch = false
                onSeekBarChanged(mProgress)
                LogUtils.d(this@PlayerFragment, "onStopTrackingTouch")
            }
        })
    }

    /**
     * 用户拖动进度条
     * @param progress Int
     */
    private fun onSeekBarChanged(progress: Int) {
        mainViewModel.playManager.seekTo(progress)
        // TODO: 2021/5/4 无效
        LogUtils.d(this, "onSeekBarChanged progress ==> $progress | position ==> position")
    }

    override fun initData() {
        mainViewModel.playManager.getTrack(mainViewModel.playPosition.value ?: 0).also { track ->
            binding.seekBarTime.max = track.duration
            LogUtils.d(this, "duration ==> ${track.duration}")
            binding.textPlayerTitle.text = track.trackTitle
        }
        binding.seekBarTime.progress = 0
        binding.textAfterTime.text =
            requireContext().getString(R.string.minutesTime, "00", "00")
        binding.textTotalTime.text = mainViewModel.playManager.duration.timeUtil()
        mInit = false
    }

    /**
     * 排序事件
     * @param view View
     */
    private fun playSortClick(view: View) {
//        TODO("Not yet implemented")
        LogUtils.d(this, "playSortClick")

        (view as ImageView).setImageDrawable(
            when ((view).drawable) {
                mPlayModeList[0] -> mPlayModeList[1]
                mPlayModeList[1] -> mPlayModeList[2]
                mPlayModeList[2] -> mPlayModeList[3]
                mPlayModeList[3] -> mPlayModeList[4]
                else -> mPlayModeList[0]
            }
        )
    }

    /**
     * 上一首事件
     * @param view View
     */
    private fun playPreviousClick(view: View) {
        LogUtils.d(this, "playPreviousClick")
    }

    /**
     * 下一首事件
     * @param view View
     */
    private fun playNextClick(view: View) {
        LogUtils.d(this, "playNextClick")
    }

    /**
     * 播放列表事件
     * @param view View
     */
    private fun playListClick(view: View) {
        LogUtils.d(this, "playListClick")
    }

    /**
     * 播放事件
     * @param view View
     */
    private fun playClick(view: View) {
        LogUtils.d(this, "playClick")
        if (!mainViewModel.playManager.isPlaying) {
            mainViewModel.play()
        } else {
            mainViewModel.stop()
        }
    }

    private fun initPlay(play: () -> Unit) {
        if (!mInit) {
            mInit = true
            play()
        }
    }
}