package com.example.mynewhumblr.data.models

data class Profile(
    var name: String?,
    var id: String?,
    var snoovatar_img: String?,
    var more_infos: UserDataSub?,
    var total_karma: Int?,
) {
    data class UserDataSub(
        var subscribers: Int?,
        var title: String?
    )
}