package com.example.mynewhumblr.data.models

data class ProfileModel(
    var name: String?,
    var id: String?,
    var urlAvatar: String?,
    var more_infos: UserDataSubscribers?,
    var total_karma: Int?,
) {
    data class UserDataSubscribers(
        var subscribers: Int?,
        var title: String?
    )
}

