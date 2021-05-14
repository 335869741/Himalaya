package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.*
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.model.track.TrackList
import kotlinx.coroutines.*
import zzz.bing.himalaya.db.AlbumSubscribeRepository
import zzz.bing.himalaya.db.entity.AlbumSubscribe
import zzz.bing.himalaya.repository.PlayerManager
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.utils.onReverse
import zzz.bing.himalaya.utils.trackSearch
import zzz.bing.himalaya.views.UILoader

class AlbumDetailViewModel : ViewModel() {

    private var mPage = 1
    private var mSearchId = 0L

    private val playerManager get() = PlayerManager.instance
    private val mTrackLiveData by lazy { MutableLiveData<List<Track>>() }

    val netState by lazy { MutableLiveData<UILoader.UIStatus>() }
    val trackLiveData: LiveData<List<Track>> by lazy { mTrackLiveData }

    val isPlaying get() = playerManager.playManager.isPlaying
    val playerState get() = playerManager.playerState
    val voice: PlayableModel get() = playerManager.playManager.currSound

    /**
     * 用id获得详情
     * @param id Long
     */
    fun getTracks(id: Long) {
        if (mPage > 1) {
            netState.value = UILoader.UIStatus.LOAD_MORE
        } else {
            netState.value = UILoader.UIStatus.LOADING
        }
        CommonRequest.getTracks(
            HashMap<String, String>().apply {
                put(DTransferConstants.ALBUM_ID, setID(id))
                put(DTransferConstants.PAGE, mPage.toString())
            },
            object : IDataCallBack<TrackList> {
                override fun onSuccess(p0: TrackList?) {
                    if (p0 == null || p0.tracks.isNullOrEmpty()) {
                        LogUtils.d(this@AlbumDetailViewModel, "onSuccess NullOrEmpty !!!")
                        netState.postValue(UILoader.UIStatus.EMPTY)
                    } else {
                        if (mPage > 1) {
                            mTrackLiveData.postValue(
                                ArrayList<Track>().apply {
                                    addAll(mTrackLiveData.value ?: emptyList())
                                    addAll(p0.tracks)
                                }
                            )
                        } else {
                            mTrackLiveData.postValue(p0.tracks)
                        }
                        netState.postValue(UILoader.UIStatus.SUCCESS)
                        mPage++
                    }
                }

                override fun onError(p0: Int, p1: String?) {
                    LogUtils.d(this@AlbumDetailViewModel, "code ==> $p0 | message ==> $p1")
                    netState.postValue(UILoader.UIStatus.NETWORK_ERROR)
                }
            })
    }

    /**
     * 提交播放列表
     * @param list List<Track>
     * @param position Int
     */
    fun putPlayList(list: List<Track>, position: Int) {
        val tracks = playerManager.playList.value ?: emptyList()
        if (tracks.isEmpty()) {
            playerManager.putPlayList(list, position)
        } else {
            val playList = if (list.first() == tracks.last()) list.onReverse() else list
            playerManager.putPlayList(playList, position)
        }
    }

    /**
     * 使用已有的播放列表继续播放或播放当前列表
     */
    fun onPlayForPlayList() {
        trackLiveData.value?.apply {
            val tracks = playerManager.playList.value ?: emptyList()
            if (tracks.isEmpty()) {
                putPlayList(this, 0)
            } else {
                if (this.first().dataId != tracks.first().dataId && this.first().dataId != tracks.last().dataId) {
                    putPlayList(this, 0)
                } else {
                    if (this.size > tracks.size) {
                        val playList =
                            if (this.first().dataId == tracks.last().dataId) this.onReverse() else this
                        val index = tracks.trackSearch(playerManager.playManager.currSound.dataId)
                        putPlayList(playList, index)
                    }
                }
            }
        }
    }

    /**
     * 搜索控制，搜索新内容时将page调整为1
     * @param id Long
     * @return String
     */
    private fun setID(id: Long): String {
        if (id != mSearchId) {
            mPage = 1
        }
        mSearchId = id
        return mSearchId.toString()
    }

    fun stop() = playerManager.stop()
    fun play() = playerManager.play()

    /**
     * 添加订阅
     * @param subscribe AlbumSubscribe
     */
    fun addSubscribe(subscribe: AlbumSubscribe) {
        AlbumSubscribeRepository.addAlbumSubscribe(subscribe)
    }

    /**
     * 删除订阅
     * @param subscribe AlbumSubscribe
     */
    fun removeSubscribe(subscribe: AlbumSubscribe) {
        AlbumSubscribeRepository.removeAlbumSubscribe(subscribe)
    }

    /**
     * 查询订阅列表，用于判断是否已订阅
     * @param album AlbumSubscribe
     * @return LiveData<List<AlbumSubscribe>>
     */
    fun getSubscribeAlbum(album: AlbumSubscribe): LiveData<List<AlbumSubscribe>> {
        return AlbumSubscribeRepository.getSubscribe(album)
    }
}