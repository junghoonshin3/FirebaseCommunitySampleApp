package kr.sjh.domain.model

import androidx.compose.runtime.Stable
import java.util.Date

@Stable
data class ChatRoomModel(
    val roomId: String,
    val recentMessage: String,
    val recentMessageTimeStamp: Date? = null,
    val users: Map<String, Map<String, Any>> = emptyMap(),
)