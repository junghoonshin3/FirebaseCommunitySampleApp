package kr.sjh.domain.usecase.login.model


data class UserInfo(
    val email: String? = null,
    val nickName: String? = null,
    val id: String? = null,
    val profileImageUrl: String? = null,
    val postCount: Int = 0,
    var likePosts: MutableList<String> = mutableListOf()
)