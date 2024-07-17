package kr.sjh.domain.model

import java.util.Date


data class ChatMessageModel(
    val messageId: String = "",
    val senderUid: String = "",
    val receiverUid: String = "",
    val profileImageUrl: String = "",
    val message: String = "",
    val timeStamp: Date? = null,
)