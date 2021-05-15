package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import zzz.bing.himalaya.db.AlbumSubscribeRepository
import zzz.bing.himalaya.db.entity.AlbumSubscribe
import zzz.bing.himalaya.repository.AlbumSubscribePaging

class ContentSubscribeViewModel : ViewModel() {

    fun getAlbum(): Flow<PagingData<AlbumSubscribe>> {
        return Pager(
            config = PagingConfig(AlbumSubscribeRepository.pageSize),
            pagingSourceFactory = { AlbumSubscribePaging() }
        ).flow.cachedIn(viewModelScope)
    }

    /**
     * 清空订阅
     */
    fun removeAlbum() {
        AlbumSubscribeRepository.removeAllAlbumSubscribe()
    }
}