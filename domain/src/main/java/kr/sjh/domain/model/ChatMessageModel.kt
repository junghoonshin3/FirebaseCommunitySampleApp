package kr.sjh.domain.model

import java.util.Date


data class ChatMessageModel(
    val messageId: String = "",
    val senderUid: String = "",
    val receiverUid: String = "",
    val text: String = "",
    val timeStamp: Date? = null,
)