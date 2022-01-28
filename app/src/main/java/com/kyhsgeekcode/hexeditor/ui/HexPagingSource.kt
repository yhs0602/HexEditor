package com.kyhsgeekcode.hexeditor.ui

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.io.IOException

class MyPagingSource(
    val byteRows: suspend (Int) -> ByteArray,
) : PagingSource<Int, ByteArray>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ByteArray> {
        return try {
            // Key may be null during a refresh, if no explicit key is passed into Pager
            // construction. Use 0 as default, because our API is indexed started at index 0
            val pageNumber = params.key ?: 0

            val fetchSize = 100
            val from = pageNumber * fetchSize
            val to = (pageNumber + 1) * fetchSize
            val response = (from until to).map { byteRows(it) }.filterNot { it.isEmpty() }

            // Since 0 is the lowest page number, return null to signify no more pages should
            // be loaded before it.
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null

            // This API defines that it's out of data when a page returns empty. When out of
            // data, we return `null` to signify no more pages should be loaded
            val nextKey = if (response.isNotEmpty()) pageNumber + 1 else null
            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ByteArray>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}