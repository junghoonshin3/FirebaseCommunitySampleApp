package kr.sjh.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ChatRoomUserEntity(
    val profileImageUrl: String = "",
    val nickName: String = "",
)