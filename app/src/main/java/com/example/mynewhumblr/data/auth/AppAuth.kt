package com.example.mynewhumblr.data.auth

import android.net.Uri
import androidx.core.net.toUri
import com.example.mynewhumblr.data.auth.models.TokensModel
import net.openid.appauth.*
import timber.log.Timber
import kotlin.coroutines.suspendCoroutine

object AppAuth {

    private val serviceConfiguration = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_URI),
        Uri.parse(AuthConfig.TOKEN_URI),
        null, // registration endpoint
        Uri.parse(AuthConfig.END_SESSION_URI)
    )

    fun getAuthRequest(): AuthorizationRequest {
        val redirectUri = AuthConfig.CALLBACK_URL.toUri()

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri
        )
            .setScope(AuthConfig.SCOPE)
            .build()
    }

    fun getEndSessionRequest(): EndSessionRequest {
        return EndSessionRequest.Builder(serviceConfiguration)
            .setPostLogoutRedirectUri(AuthConfig.LOGOUT_CALLBACK_URL.toUri())
            .build()
    }

    fun getRefreshTokenRequest(refreshToken: String): TokenRequest {
        return TokenRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setScopes(AuthConfig.SCOPE)
            .setRefreshToken(refreshToken)
            .build()
    }

    suspend fun performTokenRequestSuspend(
        authService: AuthorizationService,
        code: String?
    ): TokensModel {
        val clientAuth: ClientAuthentication = ClientSecretBasic(AuthConfig.CLIENT_SECRET)
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(
                TokenRequest.Builder(serviceConfiguration, AuthConfig.CLIENT_ID)
                .setAuthorizationCode(code).setRedirectUri(AuthConfig.CALLBACK_URL.toUri())
                .setGrantType(GrantTypeValues.AUTHORIZATION_CODE).build(), clientAuth) { response, ex ->
                when {
                    response != null -> {

                        Timber.tag("MyLog")
                            .d("!!!!!accessToken:" + response.accessToken.toString() + "\n" + "tokenType: " + response.tokenType + "\n" + "scope: " + response.scopeSet)

                        //получение токена произошло успешно
                        val tokens = TokensModel(
                            accessToken = response.accessToken.orEmpty(),
                            tokenType = response.tokenType.orEmpty(),
                            scope = response.scope.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty()
                        )
                        continuation.resumeWith(Result.success(tokens))
                    }
                    //получение токенов произошло неуспешно, показываем ошибку
                    ex != null -> { continuation.resumeWith(Result.failure(ex)) }
                    else -> error("unreachable")
                }
            }
        }
    }

    suspend fun performTokenRequestRefreshSuspend(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
    ): TokensModel {
        return suspendCoroutine { continuation ->
            authService.performTokenRequest(tokenRequest, getClientAuthentication()) { response, ex ->
                when {
                    response != null -> {
                        //получение токена произошло успешно
                        val tokens = TokensModel(
                            accessToken = response.accessToken.orEmpty(),
                            tokenType = response.tokenType.orEmpty(),
                            scope = response.scope.orEmpty(),
                            refreshToken = response.refreshToken.orEmpty()
                        )
                        continuation.resumeWith(Result.success(tokens))
                    }
                    //получение токенов произошло неуспешно, показываем ошибку
                    ex != null -> { continuation.resumeWith(Result.failure(ex)) }
                    else -> error("unreachable")
                }
            }
        }
    }


    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }

    private object AuthConfig {
        const val AUTH_URI = "https://www.reddit.com/api/v1/authorize"
        const val TOKEN_URI = "https://www.reddit.com/api/v1/access_token"
        const val END_SESSION_URI = "https://www.reddit.com/logout"
        const val RESPONSE_TYPE = ResponseTypeValues.CODE
        const val SCOPE = "identity edit flair history modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread"

        const val CLIENT_ID = "tRPeQ5qGa7M6Fmmtrm2I3A"
        const val CLIENT_SECRET = ""
        const val CALLBACK_URL = "ru.kts.oauth://github.com/callback"
        const val LOGOUT_CALLBACK_URL = "ru.kts.oauth://github.com/logout_callback"
    }
}