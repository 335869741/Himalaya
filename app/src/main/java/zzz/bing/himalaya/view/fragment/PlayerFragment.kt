package zzz.bing.himalaya.view.fragment

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.PopupWindow
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl
import zzz.bing.himalaya.BaseFragment
import zzz.bing.himalaya.R
import zzz.bing.himalaya.databinding.FragmentPlayerBinding
import zzz.bing.himalaya.repository.PlayerManager
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.utils.putAll
import zzz.bing.himalaya.utils.timeUtil
import zzz.bing.himalaya.utils.trackSearch
import zzz.bing.himalaya.view.adapter.PlayerCoverAdapter
import zzz.bing.himalaya.view.adapter.PopupPlayListAdapter
import zzz.bing.himalaya.viewmodel.PlayerViewModel
import zzz.bing.himalaya.views.PlayListPopup

@Suppress("UNNECESSARY_SAFE_CALL")
class PlayerFragment : BaseFragment<FragmentPlayerBinding, PlayerViewModel>() {

    private lateinit var mPlayerCoverAdapter: PlayerCoverAdapter
    private lateinit var mPopupPlaylist: PlayListPopup

    private var mIsInitPopup = false
    private var mIsUserTouch = false
    private var mInit = false
    private var mProgress = 0

    private val mPopupPlayListAdapter by lazy { PopupPlayListAdapter(this) }

    override fun initViewModel() = ViewModelProvider(this).get(PlayerViewModel::class.java)

    override fun initViewBinding() = FragmentPlayerBinding.inflate(layoutInflater)

    override fun initView() {
        mPlayerCoverAdapter = PlayerCoverAdapter(requireActivity())
        binding.pager.adapter = mPlayerCoverAdapter
    }

    override fun initObserver() {
        viewModel.playList.observe(viewLifecycleOwner) { trackList ->
            playListChange(trackList ?: emptyList())
        }
        viewModel.playerState.observe(viewLifecycleOwner) { playerState ->
            playerState?.also {
                playerStateChange(it)
            }
        }
        viewModel.playerBuffer.observe(viewLifecycleOwner) {
            val buffer = if (it == null || it < 0) 0 else if (it > 100) 100 else it
            seekBarBuffer(buffer)
        }
        viewModel.playerNow.observe(viewLifecycleOwner) {
            val buffer = it ?: 0
            seekBarProgress(buffer)
        }
        viewModel.playerDuration.observe(viewLifecycleOwner) {
            val duration = it ?: 0
            timeDuration(duration)
        }
        viewModel.playerMode.observe(viewLifecycleOwner) {
            it?.also { playMode ->
                playModeChange(playMode)
            }
        }
        viewModel.playOrder.observe(viewLifecycleOwner) {
            if (mIsInitPopup) {
                popupOrder(it)
            }
        }
    }

    /**
     * ????????????????????????
     * @param list List<Track>
     */
    private fun playListChange(list: List<Track>) {
        mPlayerCoverAdapter.playList.putAll(list)
        if (mIsInitPopup) {
            mPopupPlayListAdapter.submitList(list)
            val position = list.trackSearch(viewModel.nowVoice().dataId)
            mPopupPlayListAdapter.playPosition = position
            if (mPopupPlaylist.isShowing) {
                mPopupPlaylist.recycler.post {
                    mPopupPlaylist.recycler.scrollToPosition(position)
                }
            }
            LogUtils.d(this, "position ==> $position")
        }
    }

    /**
     * ??????ui??????
     * @param order Boolean
     */
    private fun popupOrder(order: Boolean) {
        //true????????? false?????????
        if (!order) {
            mPopupPlaylist.imagePlayOrder.setImageDrawable(
                getDrawable(R.drawable.selector_player_sort_descending)
            )
            mPopupPlaylist.textPlayOrder.text = "????????????"
        } else {
            mPopupPlaylist.imagePlayOrder.setImageDrawable(
                getDrawable(R.drawable.selector_player_sort_ascending)
            )
            mPopupPlaylist.textPlayOrder.text = "????????????"
        }
    }

    /**
     * ??????????????????
     * @param playMode PlayMode
     */
    private fun playModeChange(playMode: XmPlayListControl.PlayMode) {
        binding.imagePlayerMode.setImageDrawable(
            when (playMode) {
                XmPlayListControl.PlayMode.PLAY_MODEL_LIST -> {
                    getDrawable(R.drawable.selector_player_sort_descending)
                }
                XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP -> {
                    getDrawable(R.drawable.selector_play_mode_loop)
                }
                XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP -> {
                    getDrawable(R.drawable.selector_play_mode_loop_one)
                }
                else -> {
                    getDrawable(R.drawable.selector_play_mode_random)
                }
            }
        )
        popupPlayMode(playMode)
    }

    /**
     *
     * @param playMode PlayMode
     */
    private fun popupPlayMode(playMode: XmPlayListControl.PlayMode) {
        if (mIsInitPopup) {
            when (playMode) {
                XmPlayListControl.PlayMode.PLAY_MODEL_LIST -> {
                    mPopupPlaylist.imagePlayMode.setImageDrawable(getDrawable(R.drawable.selector_player_sort_descending))
                    mPopupPlaylist.textPlayMode.text = "????????????"
                }
                XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP -> {
                    mPopupPlaylist.imagePlayMode.setImageDrawable(getDrawable(R.drawable.selector_play_mode_loop))
                    mPopupPlaylist.textPlayMode.text = "????????????"
                }
                XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP -> {
                    mPopupPlaylist.imagePlayMode.setImageDrawable(getDrawable(R.drawable.selector_play_mode_loop_one))
                    mPopupPlaylist.textPlayMode.text = "????????????"
                }
                else -> {
                    mPopupPlaylist.imagePlayMode.setImageDrawable(getDrawable(R.drawable.selector_play_mode_random))
                    mPopupPlaylist.textPlayMode.text = "????????????"
                }
            }
        }
    }

    /**
     * ?????????id??????Drawable
     * @param id Int
     * @return Drawable?
     */
    private fun getDrawable(id: Int): Drawable? {
        return ContextCompat.getDrawable(
            requireContext(),
            id
        )
    }

    /**
     * ??????
     * @param duration Int
     */
    private fun timeDuration(duration: Int) {
        binding.textTotalTime.text = (duration / 1000).timeUtil()
    }

    /**
     * ??????????????????
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
     * ??????????????????
     * @param buffer Int
     */
    private fun seekBarBuffer(buffer: Int) {
        val max = binding.seekBarTime.max
        binding.seekBarTime.secondaryProgress = ((buffer * 0.01) * max).toInt()
    }

    /**
     * ??????????????????
     * @param playerState PlayerState
     */
    private fun playerStateChange(playerState: PlayerManager.PlayerState) {
        when (playerState) {
            PlayerManager.PlayerState.Playing -> {
                binding.imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_stop)
                )
            }
            PlayerManager.PlayerState.Stopped -> {
                binding.imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_start)
                )
            }
            PlayerManager.PlayerState.Usable -> {
                initPlay { viewModel.play() }
            }
            else -> {
                binding.imagePlay.setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_player_start)
                )
            }
        }
    }

    override fun initListener() {
        binding.imagePlay.setOnClickListener {
            playClick()
        }
        binding.imagePlayerList.setOnClickListener { view ->
            playListClick(view)
        }
        binding.imagePlayerNext.setOnClickListener {
            playNextClick()
        }
        binding.imagePlayerPrevious.setOnClickListener {
            playPreviousClick()
        }
        binding.imagePlayerMode.setOnClickListener {
            playModeClick()
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
        viewModel.playSwitch { _, curModel ->
            switchPlay(curModel)
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val height = binding.root.height
                if (height != 0) {
                    initPopupPlaylist(height)
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    /**
     * ??????????????????????????????
     * @param height Int
     */
    private fun initPopupPlaylist(height: Int) {
        mPopupPlaylist = PlayListPopup(height / 3 * 2, binding.root)
        mIsInitPopup = true
        mPopupPlaylist.textExit.setOnClickListener {
            popupTextExitClick(mPopupPlaylist)
        }
        mPopupPlaylist.textPlayOrder.setOnClickListener { popupPlayListReOrder() }
        mPopupPlaylist.imagePlayOrder.setOnClickListener { popupPlayListReOrder() }
        mPopupPlaylist.recycler.itemAnimator?.apply {
            changeDuration = 0
            moveDuration = 0
            removeDuration = 0
        }
        mPopupPlaylist.recycler.adapter = mPopupPlayListAdapter
        mPopupPlaylist.recycler.layoutManager = LinearLayoutManager(requireContext())
        mPopupPlayListAdapter.submitList(viewModel.playList.value)
        popupPlayMode(viewModel.playerMode.value ?: XmPlayListControl.PlayMode.PLAY_MODEL_LIST)
        popupOrder(viewModel.playOrder.value ?: true)
    }

    /**
     * ??????????????????
     */
    private fun popupPlayListReOrder() {
        viewModel.playListOrder()
    }

    /**
     *
     * popup??????text???????????????
     * @param popupWindow PopupWindow
     */
    private fun popupTextExitClick(popupWindow: PopupWindow) {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    /**
     * ??????????????????
     * @param curModel PlayableModel?
     */
    private fun switchPlay(curModel: PlayableModel?) {
        if (curModel != null && curModel is Track) {
            binding.textPlayerTitle.text = curModel.trackTitle
            val position = viewModel.playList.value.let {
                val index = it?.trackSearch(curModel.dataId)
                curModel.album?.albumId
                if (index != null && index != -1) {
                    index
                } else {
                    0
                }
            }
            binding.pager.currentItem = position
            if (mIsInitPopup) {
                mPopupPlayListAdapter.playPosition = position
                mPopupPlaylist.recycler.scrollToPosition(position)
                mPopupPlayListAdapter.notifyDataSetChanged()
            }
        }
    }


    /**
     * ?????????????????????
     * @param progress Int
     */
    private fun onSeekBarChanged(progress: Int) {
        viewModel.seekTo(progress)//playManager.seekTo(progress)
        // TODO: 2021/5/4 ??????
        LogUtils.d(this, "onSeekBarChanged progress ==> $progress | position ==> position")
    }

    override fun initData() {
        viewModel.nowVoice().also { track ->
            if (track is Track) {
                binding.seekBarTime.max = track.duration
                LogUtils.d(this, "duration ==> ${track.duration}")
                binding.textPlayerTitle.text = track.trackTitle
            }
        }
        binding.seekBarTime.progress = 0
        binding.textAfterTime.text =
            requireContext().getString(R.string.minutesTime, "00", "00")
        binding.textTotalTime.text = viewModel.duration().timeUtil()
        mInit = false
    }

    /**
     * ??????????????????
     */
    private fun playModeClick() {
        LogUtils.d(this, "playModeClick")
        viewModel.playModeSwitch()
    }

    /**
     * ???????????????
     */
    private fun playPreviousClick() {
        viewModel.playPre()
        LogUtils.d(this, "playPreviousClick")
    }

    /**
     * ???????????????
     */
    private fun playNextClick() {
        viewModel.playNext()
        LogUtils.d(this, "playNextClick")
    }

    /**
     * ??????????????????
     * @param view View
     */
    private fun playListClick(view: View) {
        if (!mPopupPlaylist.isShowing) {
            mPopupPlaylist.showAtLocation(view, Gravity.BOTTOM, 0, 0)
            val voice = viewModel.nowVoice()
            if (voice is Track) {
                val position = viewModel.playList.let { liveData ->
                    val position = liveData.value?.trackSearch(voice.dataId)
                    if (position == null || position == -1) {
                        0
                    } else {
                        position
                    }
                }
                mPopupPlayListAdapter.playPosition = position
                mPopupPlaylist.recycler.scrollToPosition(position)
            }
            LogUtils.d(this, "playListClick")
        }
    }

    /**
     * ????????????
     */
    private fun playClick() {
        LogUtils.d(this, "playClick")
        if (!viewModel.isPlaying()) {
            viewModel.play()
        } else {
            viewModel.stop()
        }
    }

    /**
     * ???????????????
     * @param play Function0<Unit>
     */
    private fun initPlay(play: () -> Unit) {
        if (!mInit) {
            mInit = true
            play()
        }
    }

    override fun onStop() {
        super.onStop()
        mPopupPlaylist.immediateDismiss()
    }

    /**
     * ???????????????????????????
     * @param adapterPosition Int
     */
    fun playToListPosition(adapterPosition: Int) {
        viewModel.play(adapterPosition)
    }
}