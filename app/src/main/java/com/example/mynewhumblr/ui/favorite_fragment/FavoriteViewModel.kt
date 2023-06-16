package com.example.mynewhumblr.ui.favorite_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.models.ApiResult
import com.example.mynewhumblr.data.models.SubredditListing
import com.example.mynewhumblr.data.models.UiText
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val pageFavoriteSubredditChildren: Flow<PagingData<SubredditListing.SubredditListingData.Subreddit>> = Pager(
        config = PagingConfig(pageSize = 25),
        pagingSourceFactory = { FavoritePagingSource(repository, "favorite_subreddits") }
    ).flow.cachedIn(viewModelScope)

    val pageFavoritePostChildren: Flow<PagingData<SubredditListing.SubredditListingData.Subreddit>> = Pager(
        config = PagingConfig(pageSize = 25),
        pagingSourceFactory = { FavoritePagingSource(repository, "favorite_posts") }
    ).flow.cachedIn(viewModelScope)

    private val _subscribeChannel = Channel<ApiResult<Int>>()
    val subscribeChannel = _subscribeChannel.receiveAsFlow()

    fun subscribeUnsubscribe(displayName: String, isUserSubscriber: Boolean, position: Int) {
        viewModelScope.launch(Dispatchers.Default)  {
//            _subscribeChannel.send(ApiResult.Loading(position))
            kotlin.runCatching {
                if (isUserSubscriber) {
                    repository.subscribeUnsubscribe("unsub", displayName)
                } else {
                    repository.subscribeUnsubscribe("sub", displayName)
                }
            }.fold(
                onSuccess = { _subscribeChannel.send(ApiResult.Success(position)) },
                onFailure = {
                    _subscribeChannel.send(
                        ApiResult.Error(UiText.ResourceString(R.string.something_went_wrong))
                    )
                }
            )
        }
    }
}