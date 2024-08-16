package kr.sjh.domain.model

import androidx.compose.runtime.Stable
import java.util.Date

@Stable
data class ChatRoomModel(
    val roomId: String,
    val recentMessage: String,
    val unReadMessageCount: Long = 0L,
    val recentMessageTimeStamp: Date? = null,
    val lastVisitedTimeStamp: Date? = null,
    val you: ChatRoomUserModel = ChatRoomUserModel()
)

