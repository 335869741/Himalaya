package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import zzz.bing.himalaya.db.AlbumSubscribeRepository
import zzz.bing.himalaya.db.entity.AlbumSubscribe

class ContentSubscribeViewModel : ViewModel() {

    /**
     * 从数据库中获得数据
     * @param page Int
     * @return LiveData<List<AlbumSubscribe>>
     */
    fun getAlbum(page: Int): LiveData<List<AlbumSubscribe>> {
        return AlbumSubscribeRepository.getAlbumSubscribe(page)
    }

    /**
     *
     */
    fun removeAlbum() {
        AlbumSubscribeRepository.removeAllAlbumSubscribe()
    }
}