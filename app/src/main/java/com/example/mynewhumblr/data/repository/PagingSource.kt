package com.example.mynewhumblr.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.ListTypes
import com.example.mynewhumblr.data.interceptor.RetrofitInterceptor.interceptor
import okhttp3.logging.HttpLoggingInterceptor

class PagingSource (
    private val repository: Repository,
    private val source: String?,
    private val listType: ListTypes
) : PagingSource<String, ListItem>() {

    override fun getRefreshKey(state: PagingState<String, ListItem>): String = FIRST_PAGE

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ListItem> {
        val page = params.key ?: FIRST_PAGE
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return kotlin.runCatching {
            repository.getList(listType, source, page)
        }.fold(
            onSuccess = {
                LoadResult.Page(data = it, prevKey = null,
                    nextKey = if (it.isEmpty()) null else it.last().name)},
            onFailure = { LoadResult.Error(it)}
        )
    }

    private companion object {
        private const val FIRST_PAGE = ""
    }
}