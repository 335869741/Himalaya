package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException
import zzz.bing.himalaya.BaseApplication
import zzz.bing.himalaya.utils.LogUtils

class MainViewModel : ViewModel(), IXmAdsStatusListener, IXmPlayerStatusListener {

    enum class PlayerState {
        Unusable, Usable, Playing, Stopped
    }

    enum class PlayerMode {
        Single, SingleLoop, List, ListLoop, Random, InvertedSequence
    }

    //Buffer max = 100 | min = 0
    private val mPlayerBuffer by lazy { MutableLiveData<Int>() }
    private val mPlayerNow by lazy { MutableLiveData<Int>() }
    private val mPlayerDuration by lazy { MutableLiveData<Int>() }
    private val mPlayerState by lazy { MutableLiveData<PlayerState>() }
    private val mPlayerMode by lazy {
        MutableLiveData<PlayerMode>().apply {
            value = PlayerMode.List
        }
    }
    private val mPlayList by lazy { MutableLiveData<List<Track>>() }
    private val mPlayPosition by lazy { MutableLiveData<Int>() }

    val playManager: XmPlayerManager by lazy {
        XmPlayerManager.getInstance(BaseApplication.getContext()).apply {
            addAdsStatusListener(this@MainViewModel)
            addPlayerStatusListener(this@MainViewModel)
            setBreakpointResume(false)
        }
    }
    val playerState: LiveData<PlayerState> by lazy { mPlayerState }
    val playerMode by lazy {
        Transformations.map(mPlayerMode) { playerMode ->
            setPlayMode(playerMode)
            playerMode
        }
    }
    val playList: LiveData<List<Track>> by lazy { mPlayList }
    val playPosition: LiveData<Int> by lazy { mPlayPosition }
    val playerBuffer: LiveData<Int> by lazy { mPlayerBuffer }
    val playerNow: LiveData<Int> by lazy { mPlayerNow }
    val playerDuration: LiveData<Int> by lazy { mPlayerDuration }
//    val playVoice: Track
//        get() {
//            return if (playList.value != null && playPosition.value != null) {
//                playList.value!![playPosition.value!!]
//            } else {//sudo apt-get remove --purge postgresql-13
//                Track()
//            }
//        }

    /**
     * 设置播放循环方式
     */
    private fun setPlayMode(playerMode: PlayerMode) {
        when (playerMode) {
            PlayerMode.InvertedSequence -> {
                invertedSequence()
                playManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST
            }
            PlayerMode.List -> {
                playManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST
            }
            PlayerMode.ListLoop -> {
                playManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP
            }
            PlayerMode.Single -> {
                playManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE
            }
            PlayerMode.SingleLoop -> {
                playManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP
            }
            PlayerMode.Random -> {
                playManager.playMode = XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM
            }
        }
    }

    /**
     * 逆序列表
     */
    private fun invertedSequence() {
        // TODO: 2021/5/1
    }

    /**
     * 设置歌单列表和歌曲在歌单的位置
     * @param list List<Track> 歌单列表
     * @param position Int 歌曲在歌单的位置
     */
    fun putPlayList(list: List<Track>, position: Int) {
        mPlayList.postValue(list)
        mPlayPosition.postValue(position)
        playManager.setPlayList(list, position)
    }

    /**
     * 开始播放
     */
    fun play() {
        if (!playManager.isPlaying
            && (playManager.playerStatus == PlayerConstants.STATE_PAUSED
                    || playManager.playerStatus == PlayerConstants.STATE_PREPARED)
        ) {
            playManager.play()
            mPlayerState.postValue(PlayerState.Playing)
            LogUtils.i(this, "playerStart | isPlaying == ${playManager.isPlaying}")
        }
        LogUtils.i(this, "playerStatus ==> ${playManager.playerStatus}")
    }

    /**
     * 停止播放
     */
    fun stop() {
        if (playManager.isPlaying) {
            playManager.pause()
            mPlayerState.postValue(PlayerState.Stopped)
            LogUtils.i(this, "playerStop | isPlaying == ${playManager.isPlaying}")
        }
    }

    //==============================================================================================
    //广告相关的回调方法
    override fun onStartGetAdsInfo() {
        LogUtils.d(this, "onStartGetAdsInfo == 广告 无")
    }

    override fun onGetAdsInfo(p0: AdvertisList?) {
        LogUtils.d(this, "onGetAdsInfo == 广告 | AdvertisList ==> $p0")
    }

    override fun onAdsStartBuffering() {
        LogUtils.d(this, "onAdsStartBuffering == 广告 无")
    }

    override fun onAdsStopBuffering() {
        LogUtils.d(this, "onAdsStopBuffering == 广告 无")
    }

    override fun onStartPlayAds(p0: Advertis?, p1: Int) {
        LogUtils.d(this, "onStartPlayAds == 广告 | Advertis ==> $p0 | code ==> $p1 ")
    }

    override fun onCompletePlayAds() {
        LogUtils.d(this, "onCompletePlayAds == 广告 无")
    }

    override fun onError(p0: Int, p1: Int) {
        LogUtils.d(this, "onError == 广告 | code1 == $p0 | code2 ==> $p1")
    }
    //广告相关的回调方法
    //==============================================================================================

    //==============================================================================================
    //播放器相关的回调方法
    override fun onPlayStart() {
        LogUtils.d(this, "播放器 == onPlayStart")
        mPlayerState.postValue(PlayerState.Playing)
    }

    override fun onPlayPause() {
        LogUtils.d(this, "播放器 == onPlayPause")
        mPlayerState.postValue(PlayerState.Stopped)
    }

    override fun onPlayStop() {
        LogUtils.d(this, "播放器 == onPlayStop")
        mPlayerState.postValue(PlayerState.Stopped)
    }

    override fun onSoundPlayComplete() {
        LogUtils.d(this, "播放器 == onSoundPlayComplete")
        mPlayerState.postValue(PlayerState.Usable)
    }

    override fun onSoundPrepared() {
        LogUtils.d(this, "播放器 == onSoundPrepared")
        mPlayerState.postValue(PlayerState.Usable)
    }

    override fun onSoundSwitch(p0: PlayableModel?, p1: PlayableModel?) {
        LogUtils.d(
            this,
            "播放器 == onSoundSwitch ||  $p0 <==> PlayableModel || $p1 <==> PlayableModel"
        )
    }

    override fun onBufferingStart() {
        LogUtils.d(this, "播放器 == onBufferingStart")
        mPlayerBuffer.postValue(0)
    }

    override fun onBufferingStop() {
        LogUtils.d(this, "播放器 == onBufferingStop")
        mPlayerBuffer.postValue(100)
    }

    override fun onBufferProgress(p0: Int) {
        mPlayerBuffer.postValue(p0)
        LogUtils.d(this, "播放器 == onBufferProgress || $p0 <==> code")
    }

    override fun onPlayProgress(p0: Int, p1: Int) {
        //计算当前进度相对总时长的百分比
        mPlayerNow.postValue(p0)
        mPlayerDuration.postValue(p1)
        LogUtils.d(this, "播放器 == onPlayProgress || $p0 <==> currPos || $p1 <==> duration")
    }

    override fun onError(p0: XmPlayerException?): Boolean {
        LogUtils.d(this, "播放器 == onError || $p0 <==> XmPlayerException")
        mPlayerState.postValue(PlayerState.Unusable)
        return false
    }
    //播放器相关的回调方法
    //==============================================================================================
}