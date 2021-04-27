package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.track.Track
import com.ximalaya.ting.android.opensdk.model.track.TrackList
import zzz.bing.himalaya.utils.LogUtils
import zzz.bing.himalaya.views.UILoader

class AlbumDetailViewModel : ViewModel() {

    private val mTrackLiveData by lazy { MutableLiveData<List<Track>>() }
    val netState by lazy { MutableLiveData<UILoader.UIStatus>() }

    val trackLiveData : LiveData<List<Track>> by lazy { mTrackLiveData }

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
}