package kr.sjh.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AuthUserModel(
    val email: String,
    val profileImageUrl: String,
    val uid: String,
    val nickName: String
)