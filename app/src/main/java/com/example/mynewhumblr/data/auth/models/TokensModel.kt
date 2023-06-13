package com.example.mynewhumblr.data.auth.models

data class TokensModel(
    val accessToken: String,
    val tokenType: String,
    val scope: String,
    val refreshToken: String
)