package kr.sjh.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ChatMessageEntity(
    val messageId: String = "",
    val senderUid: String = "",
    val receiverUid: String = "",
    val profileImageUrl: String = "",
    val message: String = "",
    @ServerTimestamp val timeStamp: Date? = null
)