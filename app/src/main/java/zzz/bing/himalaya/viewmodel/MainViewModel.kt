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

    private val playMode by lazy {
        mapOf(
            XmPlayListControl.PlayMode.PLAY_MODEL_LIST to
                    XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP,
            XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP to
                    XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE,
            XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE to
                    XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP,
            XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP to
                    XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM,
            XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM to
                    XmPlayListControl.PlayMode.PLAY_MODEL_LIST
        )
    }

    //Buffer max = 100 | min = 0
    private val mPlayerBuffer by lazy { MutableLiveData<Int>() }
    private val mPlayerNow by lazy { MutableLiveData<Int>() }
    private val mPlayerDuration by lazy { MutableLiveData<Int>() }
    private val mPlayerState by lazy { MutableLiveData<PlayerState>() }
    private val mPlayerMode by lazy {
        MutableLiveData<XmPlayListControl.PlayMode>().apply {
            value = XmPlayListControl.PlayMode.PLAY_MODEL_LIST
        }
    }
    private val mPlayList by lazy { MutableLiveData<List<Track>>() }

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
            playerMode
        }
    }
    val playList: LiveData<List<Track>> by lazy { mPlayList }
    val playerBuffer: LiveData<Int> by lazy { mPlayerBuffer }
    val playerNow: LiveData<Int> by lazy { mPlayerNow }
    val playerDuration: LiveData<Int> by lazy { mPlayerDuration }

    fun playModeSwitch() {
        val model = playMode[playManager.playMode] ?: XmPlayListControl.PlayMode.PLAY_MODEL_LIST
        mPlayerMode.postValue(model)
        playManager.playMode = model
    }

    /**
     * 设置歌单列表和歌曲在歌单的位置
     * @param list List<Track> 歌单列表
     * @param position Int 歌曲在歌单的位置
     */
    fun putPlayList(list: List<Track>, position: Int) {
        mPlayList.postValue(list)
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

    /**
     * 切换下一首歌
     */
    fun playNext() {
        if (playManager.hasNextSound()) {
            playManager.playNext()
        }
    }

    /**
     * 切换上一首歌
     */
    fun playPre() {
        if (playManager.hasPreSound()) {
            playManager.playPre()
        }
    }

    var playSwitch: ((lastModel: PlayableModel?, curModel: PlayableModel?) -> Unit)? = null

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
    /**
     *   Track{PlayableModel{dataId=411793672, kind='track',  lastPlayedMills=9519}
     *   trackTitle='又想毁约？澳大利亚借口国安问题重新审查与中企协议 | 新闻早餐 2021.5.4 星期二',
     *   coverUrlMiddle='https://imgopen.xmcdn.com/group18/M01/19/DE/wKgJKleDXj2QJYOuAAFbNixU8BI237.png!op_type=3&columns=180&rows=180',
     *   duration=507, playCount=93763, favoriteCount=162, commentCount=27, downloadCount=0,
     *   playUrl32='https://aod.cos.tx.xmcdn.com/storages/4b64-audiofreehighqps/D3/AC/CKwRIaIEZeNqAB8AgACmSQNn.mp3',
     *   playUrl64='https://aod.cos.tx.xmcdn.com/storages/4300-audiofreehighqps/53/8B/CKwRIMAEZeNuAD4AAgCmSQRF.mp3',
     *   playUrl24M4a='https://aod.cos.tx.xmcdn.com/storages/3d2b-audiofreehighqps/7E/2B/CKwRIRwEZeNWABf_0wCmSP9W.m4a',
     *   playUrl64M4a='https://aod.cos.tx.xmcdn.com/storages/0f37-audiofreehighqps/70/0A/CKwRIMAEZeNYAD7A6QCmSP-2.m4a',
     *   album=SubordinatedAlbum [albumId=4519297, albumTitle=新闻早餐（听更多新闻，上喜马拉雅APP）,
     *   coverUrlSmall=https://imgopen.xmcdn.com/group18/M01/19/DE/wKgJKleDXj2QJYOuAAFbNixU8BI237.png!op_type=5&upload_type=album&device_type=ios&name=mobile_small&magick=png,
     *   coverUrlMiddle=https://imgopen.xmcdn.com/group18/M01/19/DE/wKgJKleDXj2QJYOuAAFbNixU8BI237.png!op_type=5&upload_type=album&device_type=ios&name=medium&magick=png,
     *   coverUrlLarge=https://imgopen.xmcdn.com/group18/M01/19/DE/wKgJKleDXj2QJYOuAAFbNixU8BI237.png!op_type=5&upload_type=album&device_type=ios&name=mobile_large&magick=png,
     *   recSrc=null, recTrack=null,serializeStatus=0], createdAt=1620080400000, playSource=0, trackStatus=-1,
     *   downloadStatus=-2, sequenceId='null', isAutoPaused=false, insertSequence=0, timeline=0, downloadCreated=0,
     *   extra=false, startTime='null', endTime='null', scheduleId=0, programId=0, radioId=0, price=0.0, discountedPrice=0.0,
     *   free=false, authorized=false, isPaid=false, uid=0, priceTypeId=0, blockIndex=0, blockNum=0, chargeFileSize=0,
     *   sampleDuration=0, canDownload=false} <==> PlayableModel
     *   || Track{PlayableModel{dataId=411531027, kind='track',  lastPlayedMills=-1}
     *   trackTitle='日美安全对话，谈及中国高超音速武器 | 新闻早餐 2021.5.3 星期一',
     *   coverUrlMiddle='https://imgopen.xmcdn.com/group18/M01/19/DE/wKgJKleDXj2QJYOuAAFbNixU8BI237.png!op_type=3&columns=180&rows=180',
     *   duration=515, playCount=153539, favoriteCount=186, commentCount=39, downloadCount=0,
     *   playUrl32='https://aod.cos.tx.xmcdn.com/storages/82ea-audiofreehighqps/12/3F/CKwRIUEEZJqGAB9-NgCl4vAC.mp3',
     *   playUrl64='https://aod.cos.tx.xmcdn.com/storages/7dca-audiofreehighqps/42/50/CKwRIJEEZJqLAD77bwCl4vBp.mp3',
     *   playUrl24M4a='https://aod.cos.tx.xmcdn.com/storages/ae57-audiofreehighqps/15/3B/CKwRIW4EZJp7ABhhhACl4u5k.m4a',
     *   playUrl64M4a='https://aod.cos.tx.xmcdn.com/storages/634a-audiofreehighqps/45/23/CKwRIDoEZJp_AD_AbgCl4u8u.m4a',
     *   album=SubordinatedAlbum [albumId=4519297, albumTitle=新闻早餐（听更多新闻，上喜马拉雅APP）,
     *   coverUrlSmall=https://imgopen.xmcdn.com/group18/M01/19/DE/wKgJKleDXj2QJYOuAAFbNixU8BI237.png!op_type=5&upload_type=album&device_type=ios&name=mobile_small&magick=png,
     *   coverUrlMiddle=https://imgopen.xmcdn.com/group18/M01/19/DE/wKgJKleDXj2QJYOuAAFbNixU8BI237.png!op_type=5&upload_type=album&device_type=ios&name=medium&magick=png,
     *   coverUrlLarge=https://imgopen.xmcdn.com/group18/M01/19/DE/wKgJKleDXj2QJYOuAAFbNixU8BI237.png!op_type=5&upload_type=album&device_type=ios&name=mobile_large&magick=png,
     *   recSrc=null, recTrack=null,serializeStatus=0], createdAt=1619994000000, playSource=0, trackStatus=-1, downloadStatus=-2, sequenceId='null', isAutoPaused=false,
     *   insertSequence=0, timeline=0, downloadCreated=0, extra=false, startTime='null', endTime='null', scheduleId=0, programId=0, radioId=0, price=0.0, discountedPrice=0.0,
     *   free=false, authorized=false, isPaid=false, uid=0, priceTypeId=0, blockIndex=0, blockNum=0, chargeFileSize=0, sampleDuration=0, canDownload=false} <==> PlayableModel
     */
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
        playSwitch?.also { it(p0, p1) }
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