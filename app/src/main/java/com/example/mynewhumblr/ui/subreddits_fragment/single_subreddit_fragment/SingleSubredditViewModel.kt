package com.example.mynewhumblr.ui.subreddits_fragment.single_subreddit_fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.ListTypes
import com.example.mynewhumblr.data.LoadState
import com.example.mynewhumblr.data.SubQuery
import com.example.mynewhumblr.data.repository.PagingSource
import com.example.mynewhumblr.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleSubredditViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val _state = MutableStateFlow<LoadState>(LoadState.NotStartedYet)
    val state = _state.asStateFlow()

    val handler = CoroutineExceptionHandler { _, e ->
        _state.value = LoadState.Error("${e.message}")
    }

    fun getPostsList(name: String?): Flow<PagingData<ListItem>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = { PagingSource(repository, name, ListTypes.SUBREDDIT_POST) }
    ).flow

    fun getSubredditInfo(name: String) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            _state.value = LoadState.Loading
            _state.value = LoadState.Content(repository.getSubredditInfo(name))
        }
    }

    fun subscribe(subQuery: SubQuery) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            repository.subscribeOnSubreddit(subQuery.action, subQuery.name)
        }
    }

    fun votePost(voteDirection: Int, postName: String) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            repository.votePost(voteDirection, postName)
        }
    }

    fun savePost(postName: String) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            repository.savePost(postName)
        }
    }

    fun unsavePost(postName: String) {
        viewModelScope.launch(Dispatchers.IO + handler) {
            repository.unsavePost(postName)
        }
    }

    fun navigateBack(fragment: Fragment) {
        fragment.findNavController().navigate(
            SingleSubredditFragmentDirections.actionSingleSubredditFragmentToSubredditsFragment3()
        )
    }
}