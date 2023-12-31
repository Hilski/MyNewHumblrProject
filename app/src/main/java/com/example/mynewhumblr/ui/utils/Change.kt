package com.example.mynewhumblr.ui.utils

import com.example.mynewhumblr.data.ListTypes

class Change<T>(val value: T)

data class Query(
    var listing: ListTypes = ListTypes.SUBREDDIT,
    var source: String = "new"
)

data class FavoritesQuery(
    var listing: ListTypes = ListTypes.POST,
    var source: String = ""
)

data class SubQuery(
    var action: String = "",
    var name: String = "",
    var id: String = "",
    var voteDirection: Int = 0
)