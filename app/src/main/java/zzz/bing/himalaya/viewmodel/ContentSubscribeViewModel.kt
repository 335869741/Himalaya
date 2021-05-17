package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import zzz.bing.himalaya.db.entity.AlbumSubscribe
import zzz.bing.himalaya.repository.AlbumSubscribePaging
import zzz.bing.himalaya.repository.AlbumSubscribeRepository

class ContentSubscribeViewModel : ViewModel() {

    /**
     * 将pager转化为flow，使用viewModelScope缓存
     * @return Flow<PagingData<AlbumSubscribe>>
     */
    fun getAlbum(): Flow<PagingData<AlbumSubscribe>> {
        return Pager(
            config = PagingConfig(AlbumSubscribeRepository.pageSize),
            pagingSourceFactory = { AlbumSubscribePaging() }
        ).flow.cachedIn(viewModelScope)
    }

    /**
     * 添加订阅
     * @param subscribe AlbumSubscribe
     */
    fun addSubscribe(subscribe: AlbumSubscribe) {
        AlbumSubscribeRepository.addAlbumSubscribe(subscribe)
    }

    /**
     * 删除订阅
     * @param albumSubscribe AlbumSubscribe
     */
    fun removeAlbum(albumSubscribe: AlbumSubscribe) {
        AlbumSubscribeRepository.removeAlbumSubscribe(albumSubscribe)
    }
}