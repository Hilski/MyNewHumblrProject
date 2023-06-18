package com.example.mynewhumblr.data.models

data class PostListing(
    val kind: String,
    val data: PostListingData
) {
    data class PostListingData(
        val after: String?,
        val children: List<Post>,
        val before: Any?
    ){
        data class Post(
            val kind: String,
            val data: PostData,
        )
        data class PostData(
            val subreddit: String,
            val selftext: String?,
            val author_fullname: String,
            val saved: Boolean,
            val title: String?,
            val subreddit_name_prefixed: String,
            val name: String,
            var score: Int,
            val thumbnail: String?,
            val post_hint: String?,
            val created: Double,
            val url_overridden_by_dest: String?,
            val subreddit_id: String,
            val id: String,
            val author: String,
            val num_comments: Int,
            val permalink: String,
            val url: String?,
//            val media: Media?,
            val is_video: Boolean,
            val likes: Boolean?,
            var isUnfolded: Boolean = false,
            val body: String?,
            var user_voted: Vote = Vote.DEFAULT
        )
    }
}

enum class Vote{
    DEFAULT
}