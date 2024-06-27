package kr.sjh.domain.model

import androidx.compose.runtime.Stable


@Stable
data class UserModel(
    val uid: String? = null,
    val email: String? = null,
    var nickName: String? = null,
    val profileImageUrl: String? = null,
    val likePosts: List<String> = emptyList(),
    val myPosts: List<String> = emptyList()
)