package kr.sjh.domain.model

import java.util.Date

data class ChatRoomModel(
    val roomId: String, val message: String, val timeStamp: Date? = null
)