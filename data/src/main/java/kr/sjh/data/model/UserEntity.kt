package kr.sjh.data.model

data class UserEntity(
    val uid: String? = null,
    var nickName: String? = null,
    val profileImageUrl: String? = null,
    val likePosts: List<String> = emptyList(),
    val myPosts: List<String> = emptyList()
)