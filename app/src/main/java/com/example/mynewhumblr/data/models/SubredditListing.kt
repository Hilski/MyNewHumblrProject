package com.example.mynewhumblr.data.models

data class SubredditListing(
    val kind: String,
    val data: SubredditListingData,
) {
    data class SubredditListingData(
        val after: String?,
        val children: List<Subreddit>,
        val before: String?
    ) {
        data class Subreddit(
            val kind: String,
            val data: SubredditData,
        ) {
            data class SubredditData(
                val display_name_prefixed: String,
                val url: String?,
                val user_is_subscriber: Boolean?,
                val description: String?,
                val subscribers: Int?,
                val created: Double?,
                val id: String,
                val name: String,
                val community_icon: String?,
                val title: String?,
                val icon_img: String?,
                val display_name: String?,
                val header_img: String?,
                val banner_background_image: String?,
                val description_html: String?,
                val public_description_html: String?,
                val banner_img: String?,
                val banner_background_color: String?,
                val over18: Boolean?,
                val mobile_banner_image: String?,
            )
        }
    }
}