package com.example.mynewhumblr.data.models

import com.example.mynewhumblr.data.Thing

data class SinglePostListing(
    val kind: String,
    val data: SinglePostListingData
) {
    data class SinglePostListingData(

        val children: List<Thing>

    )
}