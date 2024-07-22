package kr.sjh.domain.model


data class AuthUserModel(
    val email: String = "",
    val profileImageUrl: String = "",
    val uid: String = "",
    val nickName: String = ""
)