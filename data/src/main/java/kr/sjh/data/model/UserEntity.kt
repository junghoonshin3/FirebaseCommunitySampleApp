package kr.sjh.data.model

import kr.sjh.domain.constant.Role

data class UserEntity(
    val uid: String = "",
    var nickName: String = "",
    val profileImageUrl: String = "",
    val likePosts: List<String> = emptyList(),
    val myPosts: List<String> = emptyList(),
    val role: String = Role.USER,
    val banUsers: List<String> = emptyList(),
    val chatRooms: List<String> = emptyList(),
)