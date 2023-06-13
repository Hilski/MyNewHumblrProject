package com.example.mynewhumblr.api

import com.example.mynewhumblr.data.models.MeResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("api/v1/me")
    suspend fun me(@Header("Authorization") authHeader:String): MeResponse
}