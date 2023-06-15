package com.example.mynewhumblr.data.models

import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.Thing

data class SinglePostListing(
    val kind: String,
    val data: SinglePostListingData
) {
    data class SinglePostListingData(

        val children: List<Children>
    ){
        data class Children(
            val data: DataChildren
        ){
            data class DataChildren(

            val selftext: String?,
            val author_fullname: String,
            val saved: Boolean,
            val title: String,
                val subreddit_name_prefixed: String,
val name: String,
            val score: Int,
            //    val thumbnail: String?,
//            val postHint: String?,
            val created: Double,
            //    val urlOverriddenByDest: String?,
//    val subredditId: String,

            val author: String,
            val num_comments: Int,
            val permalink: String,
            val url: String,
//            val fallbackUrl: String?,
            val isVideo: Boolean,
//            val likedByUser: Boolean?,
 //           var dir: Int
            )

        }
    }
}