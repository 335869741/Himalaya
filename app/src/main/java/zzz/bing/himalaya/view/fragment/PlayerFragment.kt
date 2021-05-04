package zzz.bing.himalaya.view.fragment

import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentPlayerBinding
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.utils.putAll
import zzz.bing.himalaya.utils.timeUtil
import zzz.bing.himalaya.view.adapter.PlayerAdapter
import zzz.bing.himalaya.viewmodel.MainViewModel
import zzz.bing.himalaya.viewmodel.PlayerViewModel

class PlayerFragment : BaseFragment<FragmentPlayerBinding, PlayerViewModel>() {

    private lateinit var mPlayerAdapter: PlayerAdapter

    private var mIsUserTouch = false
    private var mInit = false
    private var mProgress = 0

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
//        mainViewModel.playPosition.observe(viewLifecycleOwner) { position ->
//            binding.pager.setCurrentItem(position, false)
//        }
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
        mainViewModel.playerMode.observe(viewLifecycleOwner) { it ->
            it?.also { playMode ->
                playModeChange(playMode)
            }
        }
    }

    /**
     * 切换播放状态
     * @param playMode PlayMode
     */
    private fun playModeChange(playMode: XmPlayListControl.PlayMode) {
        when (playMode) {
            XmPlayListControl.PlayMode.PLAY_MODEL_LIST -> {
                binding.imagePlayerMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selector_player_sort_descending
                    )
                )
            }
            XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP -> {
                binding.imagePlayerMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selector_play_mode_loop
                    )
                )
            }
            XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE -> {
                binding.imagePlayerMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selector_player_list
                    )
                )
            }
            XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP -> {
                binding.imagePlayerMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selector_play_mode_loop_one
                    )
                )
            }
            else -> {
                binding.imagePlayerMode.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.selector_play_mode_random
                    )
                )
            }
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
        binding.imagePlayerMode.setOnClickListener { view ->
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
        mainViewModel.playSwitch = { lastModel, curModel ->
            switchPlay(lastModel, curModel)
        }
    }

    /**
     * 歌曲切换事件
     * @param lastModel PlayableModel?
     * @param curModel PlayableModel?
     */
    private fun switchPlay(lastModel: PlayableModel?, curModel: PlayableModel?) {
        if (curModel != null && curModel is Track) {
            binding.textPlayerTitle.text = curModel.trackTitle
            val position = binding.pager.currentItem
            binding.pager.currentItem =
                if (lastModel == null || lastModel.dataId > curModel.dataId) {
                    position + 1
                } else {
                    position - 1
                }
        }
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
        mainViewModel.playManager.currSound.also { track ->
            if (track is Track) {
                binding.seekBarTime.max = track.duration
                LogUtils.d(this, "duration ==> ${track.duration}")
                binding.textPlayerTitle.text = track.trackTitle
            }
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
        mainViewModel.playModeSwitch()
    }

    /**
     * 上一首事件
     * @param view View
     */
    private fun playPreviousClick(view: View) {
        mainViewModel.playPre()
        LogUtils.d(this, "playPreviousClick")
    }

    /**
     * 下一首事件
     * @param view View
     */
    private fun playNextClick(view: View) {
        mainViewModel.playNext()
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