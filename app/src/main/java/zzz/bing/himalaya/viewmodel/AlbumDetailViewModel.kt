package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.model.track.TrackList
import zzz.bing.himalaya.repository.PlayerManager
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.utils.onReverse
import zzz.bing.himalaya.utils.trackSearch
import zzz.bing.himalaya.views.UILoader

class AlbumDetailViewModel : ViewModel() {

    private val playerManager get() = PlayerManager.instance
    private val mTrackLiveData by lazy { MutableLiveData<List<Track>>() }
    val netState by lazy { MutableLiveData<UILoader.UIStatus>() }

    val trackLiveData: LiveData<List<Track>> by lazy { mTrackLiveData }
    val isPlaying get() = playerManager.playManager.isPlaying
    val playerState get() = playerManager.playerState
    val voice get() = playerManager.playManager.currSound

    fun getTracksOrNull(id: Long) {
        netState.postValue(UILoader.UIStatus.LOADING)
        CommonRequest.getTracks(
            HashMap<String, String>().apply {
                put(DTransferConstants.ALBUM_ID, "$id")
//                put(DTransferConstants.SORT, "asc")
//                put(DTransferConstants.PAGE, "1")
            },
            object : IDataCallBack<TrackList> {
                override fun onSuccess(p0: TrackList?) {
                    if (p0?.tracks.isNullOrEmpty()) {
                        LogUtils.d(this@AlbumDetailViewModel, "onSuccess NullOrEmpty !!!")
                        netState.postValue(UILoader.UIStatus.EMPTY)
                    } else {
                        mTrackLiveData.postValue(p0?.tracks)
                        netState.postValue(UILoader.UIStatus.SUCCESS)
                    }
                }

                override fun onError(p0: Int, p1: String?) {
                    LogUtils.d(this@AlbumDetailViewModel, "code ==> $p0 | message ==> $p1")
                    netState.postValue(UILoader.UIStatus.NETWORK_ERROR)
                }
            })
    }

    /**
     *
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
     *
     */
    fun onPlayForPlayList() {
        trackLiveData.value?.apply {
            val tracks = playerManager.playList.value ?: emptyList()
            if (tracks.isEmpty()) {
                putPlayList(this, 0)
            } else {
                if (this.first() != tracks.first() || this.first() != tracks.last()) {
                    putPlayList(this, 0)
                } else {
                    if (this.size > tracks.size) {
                        val playList = if (this.first() == tracks.last()) this.onReverse() else this
                        val index = tracks.trackSearch(playerManager.playManager.currSound.dataId)
                        putPlayList(playList, index)
                    }
                }
            }
        }
    }

    fun stop() = playerManager.stop()
    fun play() = playerManager.play()
}