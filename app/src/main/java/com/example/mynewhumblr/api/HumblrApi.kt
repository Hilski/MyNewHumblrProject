package com.example.mynewhumblr.api

import com.example.mynewhumblr.data.models.ClickedUserProfile
import com.example.mynewhumblr.data.models.Comments
import com.example.mynewhumblr.data.models.PostListing
import com.example.mynewhumblr.data.models.SubredditListing
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface HumblrApi {
    @GET("/subreddits/{source}")
    suspend fun getSubredditListing(
        @Path("source") source: String?,
        @Query("after") page: String,
        @Header("Authorization") authHeader: String
    ): SubredditListing

    @GET("/{source}")
    suspend fun getSubredditPosts(
        @Path("source") source: String?,
        @Query("after") page: String,
        @Header("Authorization") authHeader: String
    ): PostListing

    @GET("/r/popular/{source}")
    suspend fun getPostListing(
        @Path("source") source: String?,
        @Query("after") page: String,
        @Header("Authorization") authHeader: String
    ): PostListing

    @GET("/subreddits/mine/subscriber")
    suspend fun getSubscribed(
        @Query("after") page: String?,
        @Header("Authorization") authHeader: String
    ): SubredditListing

    @GET("/subreddits/search")
    suspend fun searchSubredditsPaging(
        @Query("q") source: String?,
        @Query("after") page: String?,
        @Header("Authorization") authHeader: String
    ): SubredditListing

    @POST("/api/subscribe")
    suspend fun subscribeOnSubreddit(
        @Query("action") action: String,
        @Query("sr") name: String,
        @Header("Authorization") authHeader: String
    )

    @GET("/{source}/about")
    suspend fun getSubredditInfo(
        @Path("source") source: String?,
        @Header("Authorization") authHeader: String
    ): SubredditListing.SubredditListingData.Subreddit

    @POST("/api/vote")
    suspend fun votePost(
        @Query("dir") dir: Int,
        @Query("id") postName: String,
        @Header("Authorization") authHeader: String
    )

    @POST("/api/save")
    suspend fun savePost(
        @Query("id") postName: String,
        @Header("Authorization") authHeader: String
    )

    @POST("/api/unsave")
    suspend fun unsavePost(
        @Query("id") postName: String,
        @Header("Authorization") authHeader: String
    )

    @GET("/user/{username}/about")
    suspend fun getAnotherUserProfile(
        @Path("username") username: String,
        @Header("Authorization") authHeader: String
    ): ClickedUserProfile

    //Ниже апи запроса комментариев, создать модель
    @GET("/comments/{id}/.json")
    suspend fun getComments(
        @Path("id") id: String,
        @Header("Authorization") authHeader: String,
        @Query("depth") depth: Int = 1,
        @Query("context") context: Int = 2,
        @Query("showmedia") showmedia: Boolean = false,
        @Query("limit") limit: Int = 10
    ): Comments


    @PUT("/api/v1/me/friends/{username}")
    suspend fun makeFriends(
        @Path("username") userName: String,
        @Header("Authorization") authHeader: String,
        @Body requestBody: RequestBody = RequestBody(userName)
    )


    @GET("/user/{username}")
    suspend fun getUserContent(
        @Path("username") username: String,
        @Header("Authorization") authHeader: String
    ): PostListing

    @POST("/api/subscribe")
    suspend fun subscribeUnsubscribe(
        @Header("Authorization") token: String,
        @Query("action") action: String,
        @Query("sr_name") displayName: String
    )

    /*    @GET("/subreddits/new")
        suspend fun getNewSubreddits(
            @Header("Authorization") token: String,
            @Query("after") afterKey: String
        ): SubredditListing
     */

    @GET("/user/{username}/saved/")
    suspend fun getSaved(
        @Path("username") username: String?,
        @Query("after") page: String,
        @Header("Authorization") token: String
    ): PostListing

    @GET("/subreddits/mine/subscriber")   //ЛИШНЕЕ
    suspend fun loadFavoriteSubreddits(
        @Header("Authorization") token: String,
        @Query("after") afterKey: String
    ): SubredditListing

@GET("/user/{userName}/saved")
suspend fun loadFavoritePosts(
    @Header("Authorization") token: String,
    @Path("userName") userName: String,
    @Query("after") after: String,
    @Query("type") type: String = "links"
): PostListing
}


data class RequestBody(val name: String)