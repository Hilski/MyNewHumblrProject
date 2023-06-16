package com.example.mynewhumblr.ui.favorite_fragment

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mynewhumblr.data.models.SubredditListing
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.data.repository.Repository
import javax.inject.Inject

class FavoritePagingSource @Inject constructor(
    private val repository: Repository,
    private val methodType: String
) : PagingSource<String, SubredditListing.SubredditListingData.Subreddit>() {
    override fun getRefreshKey(state: PagingState<String, SubredditListing.SubredditListingData.Subreddit>): String = ""

    override suspend fun load(params: LoadParams<String>): LoadResult<String, SubredditListing.SubredditListingData.Subreddit> {
        val after = params.key ?: ""
        return kotlin.runCatching {
            when (methodType) {
//                "new" -> repository.getNewSubreddits(after)
//                "popular" -> repository.getPopularSubreddits(after)
//                "posts" -> repository.loadSubredditPosts(subredditName = subredditName, after)
//                "search" -> repository.searchSubreddits(query = searchQuery, after)
                "favorite_subreddits" -> repository.loadFavoriteSubreddits(after)
                "favorite_posts" -> repository.loadFavoritePosts(
                    after,
                    userName = userName,
                    type = "links"
                )

                else -> throw IllegalArgumentException("Invalid method type")
            }
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.data.children,
                    prevKey = null,
                    nextKey = it.data.after
                )
            },
            onFailure = { LoadResult.Error(it) }
        )
    }

    companion object {
        var subredditName = ""
        var searchQuery = "best"
        var userName = ""
    }
}