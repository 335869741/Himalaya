package zzz.bing.himalaya.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import zzz.bing.himalaya.db.entity.AlbumHistory
import zzz.bing.himalaya.repository.AlbumHistoryPaging
import zzz.bing.himalaya.repository.AlbumHistoryRepository

class ContentHistoryViewModel : ViewModel() {

    /**
     * 将pager转化为flow，使用viewModelScope缓存
     * @return Flow<PagingData<AlbumHistory>>
     */
    fun getAlbum(): Flow<PagingData<AlbumHistory>> {
        return Pager(
            config = PagingConfig(AlbumHistoryRepository.pageSize),
            pagingSourceFactory = { AlbumHistoryPaging() }
        ).flow.cachedIn(viewModelScope)
    }
}