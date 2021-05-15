package zzz.bing.himalaya.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import zzz.bing.himalaya.db.AlbumSubscribeRepository
import zzz.bing.himalaya.db.entity.AlbumSubscribe

class AlbumSubscribePaging : PagingSource<Int, AlbumSubscribe>() {
    override fun getRefreshKey(state: PagingState<Int, AlbumSubscribe>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AlbumSubscribe> {
        val page = params.key ?: 1
        val prevKey = if (page > 1) page - 1 else null
        val albumSubscribe = AlbumSubscribeRepository.getAlbumSubscribe(page)
        val nextKey = if (albumSubscribe.isNullOrEmpty()) null else page + 1
        return LoadResult.Page(albumSubscribe, prevKey, nextKey)
    }
}