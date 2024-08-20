package kr.sjh.domain.model

import androidx.compose.runtime.Stable
import kr.sjh.domain.constant.Role
import java.util.Date


@Stable
data class UserModel(
    val uid: String = "",
    val email: String = "",
    var nickName: String = "",
    val profileImageUrl: String = "",
    val likePosts: List<String> = emptyList(),
    val myPosts: List<String> = emptyList(),
    val role: String = Role.USER,
    val banUsers: List<String> = emptyList(),
)