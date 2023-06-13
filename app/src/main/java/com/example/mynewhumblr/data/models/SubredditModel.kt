package com.example.mynewhumblr.data.models

import com.example.mynewhumblr.data.ListItem

data class SubredditModel(
    val namePrefixed: String,
    val url: String?,
    val imageUrl: String?,
    val isUserSubscriber: Boolean?,
    val description: String?,
    val subscribers: Int?,
    val created: Double?,
    override val id: String,
    override val name: String
): ListItem