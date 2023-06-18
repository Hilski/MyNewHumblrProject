package com.example.mynewhumblr.ui.subreddits_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.ListTypes
import com.example.mynewhumblr.data.LoadState
import com.example.mynewhumblr.data.Query
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.data.repository.PagingSource
import com.example.mynewhumblr.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubredditsViewModel  @Inject constructor(
    private val repository: Repository
): ViewModel(){

    private val _query = Query()
    private val subredditsSource get() = _query.source

    private val _subredditsSourceFlow = MutableStateFlow(subredditsSource)

    val _state = MutableStateFlow<LoadState>(LoadState.NotStartedYet)
    val state = _state.asStateFlow()

    val handler = CoroutineExceptionHandler { _, e ->
        _state.value = LoadState.Error("${e.message}")
    }

    fun setSource(position: Int) {
        _query.source = if (position == FIRST_POSITION_INDEX) NEW else POPULAR
        _subredditsSourceFlow.value = subredditsSource
        getSubreddits()
    }

    fun getSubreddits() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            _state.value = LoadState.Loading
            getSubredditsList(subredditsSource, ListTypes.SUBREDDIT)
            _state.value = LoadState.Content()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    var subredditsList: Flow<PagingData<ListItem>> =
        _subredditsSourceFlow.asStateFlow().flatMapLatest { source ->
            if ((source == POPULAR) || (source == NEW)) {
                getSubredditsList(source, ListTypes.SUBREDDIT).flow
            } else {
                getSubredditsList(source, ListTypes.SUBREDDITS_SEARCH).flow
            }
        }.cachedIn(CoroutineScope(Dispatchers.IO))


    private fun getSubredditsList(
        source: String?,
        listType: ListTypes
    ): Pager<String, ListItem> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE_SUBREDDITS),
            pagingSourceFactory = { PagingSource(repository, source, listType) }
        )

    fun subscribe(subQuery: SubQuery) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            repository.subscribeOnSubreddit(subQuery.action, subQuery.name)
        }
    }

    fun onSearchButtonClick(text: String) {
        viewModelScope.launch(Dispatchers.Main + handler) {
            _state.value = LoadState.Loading
            _query.source = text
            _subredditsSourceFlow.value = subredditsSource
            getSubredditsList(text, ListTypes.SUBREDDITS_SEARCH)
            _state.value = LoadState.Content()
        }
    }

    companion object {
        private const val FIRST_POSITION_INDEX = 0
        private const val NEW = "new"
        private const val POPULAR = "popular"
        private const val PAGE_SIZE_SUBREDDITS = 10
    }
}