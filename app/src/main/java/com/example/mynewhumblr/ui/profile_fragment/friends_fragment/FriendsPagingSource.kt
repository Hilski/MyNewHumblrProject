package com.example.mynewhumblr.ui.profile_fragment.friends_fragment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.data.repository.Repository
import javax.inject.Inject

class FriendsPagingSource @Inject constructor(
    private val repository: Repository
) : PagingSource<String, UserFriends.Data.Children>(){
    override fun getRefreshKey(state: PagingState<String, UserFriends.Data.Children>): String = ""

    override suspend fun load(params: LoadParams<String>): LoadResult<String, UserFriends.Data.Children> {
        return kotlin.runCatching {
            repository.getUserFriends()
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.data.children,
                    prevKey = null,
                    nextKey = null
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }
}