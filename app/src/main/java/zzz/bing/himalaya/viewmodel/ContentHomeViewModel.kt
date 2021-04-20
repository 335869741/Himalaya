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
import zzz.bing.himalaya.utils.LogUtil

class ContentHomeViewModel : ViewModel() {

    private val mAlbumList by lazy { MutableLiveData<List<Album>>() }

    val albumList : LiveData<List<Album>> by lazy { mAlbumList }

    /**
     * 获取推荐专辑列表
     */
    fun getRecommendAlbum(){
        val map: MutableMap<String, String> = HashMap()
        //控制获取专辑数量
        map[DTransferConstants.LIKE_COUNT] = Constants.RECOMMEND_COUNT.toString()
        CommonRequest.getGuessLikeAlbum(map, object : IDataCallBack<GussLikeAlbumList> {
            override fun onSuccess(p0: GussLikeAlbumList?) {
                LogUtil.d(this@ContentHomeViewModel,
                    "getGuessLikeAlbum size --> ${p0?.albumList?.size}")
                mAlbumList.postValue(p0?.albumList)
            }

            override fun onError(p0: Int, p1: String?) {
                LogUtil.e(this@ContentHomeViewModel,
                    "getGuessLikeAlbum onError code ==> $p0 | message ==> $p1")
            }
        })
    }
}