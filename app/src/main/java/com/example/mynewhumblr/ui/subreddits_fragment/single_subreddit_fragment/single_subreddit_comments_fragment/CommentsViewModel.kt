package com.example.mynewhumblr.ui.subreddits_fragment.single_subreddit_fragment.single_subreddit_comments_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mynewhumblr.data.models.Comments
import com.example.mynewhumblr.data.repository.CommentsPagingSource
import com.example.mynewhumblr.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val pageComments: Flow<PagingData<Comments.Data.Children>> = Pager(
        config = PagingConfig(pageSize = 25),
        pagingSourceFactory = { CommentsPagingSource(repository) }
    ).flow.cachedIn(viewModelScope)
}