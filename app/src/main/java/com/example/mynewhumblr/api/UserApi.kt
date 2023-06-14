package com.example.mynewhumblr.api

import com.example.mynewhumblr.data.models.MeResponse
import com.example.mynewhumblr.data.models.UserFriends
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UserApi {
    @GET("api/v1/me")
    suspend fun me(@Header("Authorization") authHeader:String): MeResponse

    @GET("/api/v1/me/friends")
    suspend fun getUserFriends(
        @Header("Authorization") token: String,
    ): UserFriends

    @DELETE("/api/v1/me/friends/{username}")
    suspend fun doNotMakeFriends(
        @Header("Authorization") token: String,
        @Path("username") userName: String,
    )
}