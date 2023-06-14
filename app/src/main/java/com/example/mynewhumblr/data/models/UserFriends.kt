package com.example.mynewhumblr.data.models

import com.google.gson.annotations.SerializedName

data class UserFriends(
    val `data`: Data,
    val kind: String
){
    data class Data(
        val children: List<Children>
    ){
        data class Children(
            val date: Int,
            val id: String,
            val name: String,
            val rel_id: String
        )
    }
}