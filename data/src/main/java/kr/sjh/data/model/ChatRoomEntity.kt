package kr.sjh.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ChatRoomEntity(
    val roomId: String = "",
    val recentMessage: String = "",
    val unReadMessageCount: Long = 0L,
    @ServerTimestamp val recentMessageTimeStamp: Timestamp? = null,
    @ServerTimestamp val lastVisitedTimeStamp: Timestamp? = null,
    val you: ChatRoomUserEntity = ChatRoomUserEntity()
)