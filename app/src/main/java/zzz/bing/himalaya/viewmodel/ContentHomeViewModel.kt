package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack
import com.ximalaya.ting.android.opensdk.model.album.Album
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList
import zzz.bing.himalaya.utils.Constants
import zzz.bing.himalaya.utils.UtilLog
import zzz.bing.himalaya.views.UILoader
import java.util.HashMap

class ContentHomeViewModel : ViewModel() {


    private val mUIStatusLiveData by lazy {
        MutableLiveData<UILoader.UIStatus>().apply { value = UILoader.UIStatus.NONE }
    }
    private val mAlbumList by lazy { MutableLiveData<List<Album>>() }

    val albumList: LiveData<List<Album>> by lazy { mAlbumList }
    val uiStatusLiveData: LiveData<UILoader.UIStatus> by lazy { mUIStatusLiveData }

    /**
     * 获取推荐专辑列表
     */
    fun getRecommendAlbum() {
        mUIStatusLiveData.postValue(UILoader.UIStatus.LOADING)
        CommonRequest.getGuessLikeAlbum(
            //控制获取专辑数量
            HashMap<String,String>().apply {
                put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT.toString())
            }
            , object : IDataCallBack<GussLikeAlbumList> {
            override fun onSuccess(p0: GussLikeAlbumList?) {
                UtilLog.d(this@ContentHomeViewModel,
                    "getGuessLikeAlbum size --> ${p0?.albumList?.size}"
                )
                //判空
                if (p0 != null &&
                    !p0.albumList.isNullOrEmpty()
                ) {
                    mAlbumList.postValue(p0.albumList)
                    mUIStatusLiveData.postValue(UILoader.UIStatus.SUCCESS)
                } else {
                    mUIStatusLiveData.postValue(UILoader.UIStatus.EMPTY)
                }
            }

            override fun onError(p0: Int, p1: String?) {
                UtilLog.e(this@ContentHomeViewModel,
                    "getGuessLikeAlbum onError code ==> $p0 | message ==> $p1"
                )
                mUIStatusLiveData.postValue(UILoader.UIStatus.NETWORK_ERROR)
            }
        })
    }
}