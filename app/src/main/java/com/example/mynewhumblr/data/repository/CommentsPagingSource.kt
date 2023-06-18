package com.example.mynewhumblr.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mynewhumblr.data.models.Comments
import javax.inject.Inject

class CommentsPagingSource @Inject constructor(
    private val repository: Repository
) : PagingSource<String, Comments.Data.Children>(){
    override fun getRefreshKey(state: PagingState<String, Comments.Data.Children>): String = ""

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Comments.Data.Children> {
        return kotlin.runCatching {
            repository.getComments(postId = postId)
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it[1].data.children,
                    prevKey = null,
                    nextKey = it[1].data.after
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    companion object{
        var postId = ""
    }
}