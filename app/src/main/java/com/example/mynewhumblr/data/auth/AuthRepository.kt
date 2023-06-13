package com.example.mynewhumblr.data.auth

import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import net.openid.appauth.TokenRequest
import timber.log.Timber

class AuthRepository {

    fun corruptAccessToken() {
        TokenStorage.accessToken = "fake token"
    }

    fun logout() {
        TokenStorage.accessToken = null
        TokenStorage.tokenType = null
        TokenStorage.scope = null
        TokenStorage.refreshToken = null

    }

    fun getAuthRequest(): AuthorizationRequest {
        return AppAuth.getAuthRequest()
    }

    fun getEndSessionRequest(): EndSessionRequest {
        return AppAuth.getEndSessionRequest()
    }

    suspend fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ) {
        val code = tokenRequest.authorizationCode
        val tokens = AppAuth.performTokenRequestSuspend(authService, code)
        //обмен кода на токен произошел успешно, сохраняем токены и завершаем авторизацию
        TokenStorage.accessToken = tokens.accessToken
        TokenStorage.tokenType = tokens.tokenType
        TokenStorage.scope = tokens.scope
        TokenStorage.refreshToken = tokens.refreshToken

        Timber.tag("Oauth")
            .d(
                "6. Tokens accepted:\n accessToken=${tokens.accessToken}\n" +
                        " tokenType=${tokens.tokenType}\n" +
                        " scope=${tokens.scope}\n refreshToken=${tokens.refreshToken}"
            )
    }
}