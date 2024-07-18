package kr.sjh.data.model

import java.util.Date


data class ChatRoomEntity(
    val roomId: String = "",
    val message: String = "",
    val timeStamp: Date? = null
)