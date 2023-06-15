package com.example.mynewhumblr.ui.profile_fragment.friends_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mynewhumblr.R
import com.example.mynewhumblr.data.models.ApiResult
import com.example.mynewhumblr.data.models.UiText
import com.example.mynewhumblr.data.models.UserFriends
import com.example.mynewhumblr.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val pageFriends: Flow<PagingData<UserFriends.Data.Children>> = Pager(
        config = PagingConfig(pageSize = 25),
        pagingSourceFactory = { FriendsPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)

    private val _doNotMakeFriendsChannel = Channel<ApiResult<String>>()
    val doNotMakeFriendsChannel = _doNotMakeFriendsChannel.receiveAsFlow()

    fun doNotMakeFriends(userName: String) {
        viewModelScope.launch {
            runCatching {
                repository.doNotMakeFriends(userName)
            }.onSuccess {
                _doNotMakeFriendsChannel.send(ApiResult.Success("Success"))
            }.onFailure {
                _doNotMakeFriendsChannel.send(ApiResult.Error(UiText.ResourceString(R.string.something_went_wrong)))
            }
        }
    }
}