package zzz.bing.himalaya.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import zzz.bing.himalaya.db.entity.AlbumHistory

class AlbumHistoryPaging : PagingSource<Int, AlbumHistory>() {
    override fun getRefreshKey(state: PagingState<Int, AlbumHistory>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AlbumHistory> {
        val page = params.key ?: 1
        val prevKey = if (page > 1) page - 1 else null
        val albumHistory = AlbumHistoryRepository.getAlbumHistory(page)
        val nextKey = if (albumHistory.isNullOrEmpty()) null else page + 1
        return LoadResult.Page(albumHistory, prevKey, nextKey)
    }
}