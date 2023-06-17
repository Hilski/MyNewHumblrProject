package com.example.mynewhumblr.data.repository

import com.example.mynewhumblr.api.HumblrApi
import com.example.mynewhumblr.api.UserApi
import com.example.mynewhumblr.data.ListItem
import com.example.mynewhumblr.data.ListTypes
import com.example.mynewhumblr.data.auth.TokenStorage
import com.example.mynewhumblr.data.models.Comments
import com.example.mynewhumblr.data.models.MeResponse
import com.example.mynewhumblr.data.models.PostListing
import com.example.mynewhumblr.data.models.PostModel
import com.example.mynewhumblr.data.models.Profile
import com.example.mynewhumblr.data.models.ProfileModel
import com.example.mynewhumblr.data.models.SubredditListing
import com.example.mynewhumblr.data.models.SubredditModel
import com.example.mynewhumblr.data.models.UserFriends
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val humblrApi: HumblrApi,
    private val userApi: UserApi

) {
    private val token = "Bearer ${TokenStorage.accessToken}"


    suspend fun getUserInformation(): MeResponse {
        return userApi.me(token)
    }

    suspend fun getList(type: ListTypes, source: String?, page: String): List<ListItem> {
        return when (type) {
            ListTypes.SUBREDDIT -> humblrApi.getSubredditListing(source, page, token)
                .data.children.toListSubreddit()

            ListTypes.SUBREDDIT_POST -> humblrApi.getSubredditPosts(source, page, token)
                .data.children.toListPost()

            ListTypes.POST -> humblrApi.getPostListing(
                source,
                page,
                token
            ).data.children.toListPost()

            ListTypes.SUBSCRIBED_SUBREDDIT -> humblrApi.getSubscribed(page, token)
                .data.children.toListSubreddit()

            //Доделать
            ListTypes.SAVED_POST -> {
                val username = userApi.me(token).name
                humblrApi.getSaved(username, page, token).data.children.toListPost()
            }

            ListTypes.SUBREDDITS_SEARCH -> humblrApi.searchSubredditsPaging(
                source,
                page,
                token
            ).data.children.toListSubreddit()


        }
    }

    suspend fun subscribeOnSubreddit(action: String, name: String) =
        humblrApi.subscribeOnSubreddit(action, name, token)

    suspend fun getSubredditInfo(name: String): SubredditModel {
        return humblrApi.getSubredditInfo(name, token).toSubreddit()
    }

    suspend fun votePost(dir: Int, postName: String) = humblrApi.votePost(dir, postName, token)

    suspend fun savePost(postName: String) = humblrApi.savePost(postName, token)

    suspend fun unsavePost(postName: String) = humblrApi.unsavePost(postName, token)


//    suspend fun getLoggedUserProfile(): Profile = apiProfile.getLoggedUserProfile().toProfile()

//    suspend fun getFriends(): Friends = apiProfile.getFriends().data.toFriends()

    suspend fun getAnotherUserProfile(username: String): ProfileModel =
        humblrApi.getAnotherUserProfile(username, token).data.toProfile()

    suspend fun getComments(id: String): Comments = humblrApi.getComments(id, token)

    suspend fun makeFriends(username: String) = humblrApi.makeFriends(username, token)

    /** no comments in tech.reqs, but can add later, after comments view implementation*/
    suspend fun getUserContent(username: String): List<ListItem> {
        val list = mutableListOf<ListItem>()
        humblrApi.getUserContent(username, token).data.children.forEach { child ->
            child as PostListing.PostListingData.Post
            list.add(child.toPostModel())
            //           if (child is Post) list.add(child.toPostModel())
        }
        return list.toList()
    }

    /*   suspend fun getNewSubreddits(afterKey: String): SubredditListing {
           return humblrApi.getNewSubreddits(token , afterKey = afterKey)
       }

     */
    suspend fun loadFavoriteSubreddits(afterKey: String): SubredditListing {
        return humblrApi.loadFavoriteSubreddits(token, afterKey = afterKey)
    }

    suspend fun loadFavoritePosts(
        afterKey: String,
        userName: String,
        type: String
    ): PostListing {
        val username = userApi.me(token).name ?: ""
        return humblrApi.loadFavoritePosts(token, username, after = afterKey)
    }



    suspend fun getUserFriends(): UserFriends {
        return userApi.getUserFriends(token)
    }

    suspend fun doNotMakeFriends(userName: String) {
        userApi.doNotMakeFriends(token, userName = userName)
    }


    /*    suspend fun getUserContent(username: String): SinglePostListing {

            return humblrApi.getUserContent(username, token)
        }

     */
    suspend fun subscribeUnsubscribe(action: String, displayName: String) {
        humblrApi.subscribeUnsubscribe(token, action, displayName)
    }

    fun List<SubredditListing.SubredditListingData.Subreddit>.toListSubreddit(): List<SubredditModel> =
        this.map { item -> item.toSubreddit() }

    fun SubredditListing.SubredditListingData.Subreddit.toSubreddit(): SubredditModel {

        var url = data.community_icon
        if (url != null) {

            if (url.contains('?')) {
                val questionMark = url.indexOf('?', 0)
                url = url.substring(0, questionMark)
            }
        } else url = data.icon_img

        return SubredditModel(
            namePrefixed = data.display_name_prefixed,
            url = data.url,
            imageUrl = url,
            isUserSubscriber = data.user_is_subscriber,
            description = data.description,
            subscribers = data.subscribers,
            created = data.created,
            id = data.id,
            name = data.name
        )
    }

    fun List<PostListing.PostListingData.Post>.toListPost(): List<PostModel> =
        this.map { item -> item.toPostModel() }

    fun PostListing.PostListingData.Post.toPostModel(): PostModel {
        val voteDirection = when (data.likes) {
            null -> 0
            true -> 1
            false -> -1
        }
        return PostModel(
            selfText = data.selftext,
            authorFullname = data.author_fullname,
            saved = data.saved,
            title = data.title ?: "",
            subredditNamePrefixed = data.subreddit_name_prefixed,
            name = data.name,
            score = data.score,
            postHint = data.post_hint,
            created = data.created,
            id = data.id,
            author = data.author,
            numComments = data.num_comments,
            permalink = data.permalink,
            url = data.url ?: "",
            fallbackUrl = null,
//            fallbackUrl = data.media?.reddit_video?.fallback_url,
            isVideo = data.is_video,
            likedByUser = data.likes,
            dir = voteDirection
        )
    }

    fun Profile.toProfile() =
        ProfileModel(
            name = name,
            id = id,
            urlAvatar = snoovatar_img,
            more_infos = more_infos?.toUserDataSub(),
            total_karma = total_karma
        )

    fun Profile.UserDataSub.toUserDataSub() = ProfileModel.UserDataSubscribers(subscribers, title)

}