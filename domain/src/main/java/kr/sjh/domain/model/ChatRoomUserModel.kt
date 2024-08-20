package kr.sjh.domain.model

import androidx.compose.runtime.Stable
import java.util.Date

@Stable
data class ChatRoomUserModel(
    val profileImageUrl: String = "",
    val nickName: String = "",
)