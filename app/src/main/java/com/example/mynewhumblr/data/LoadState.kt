package com.example.mynewhumblr.data

sealed interface LoadState {
    object NotStartedYet : LoadState
    object Loading : LoadState

    data class Content(var data: Any? = null, var data2: Any? = null) : LoadState
//    data class CommentsContent(var data: Comments? = null) : LoadState
    data class Error(var message: String = "") : LoadState
}