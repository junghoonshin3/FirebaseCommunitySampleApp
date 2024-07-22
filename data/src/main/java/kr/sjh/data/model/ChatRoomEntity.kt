package kr.sjh.data.model

import java.util.Date


data class ChatRoomEntity(
    val roomId: String = "",
    val recentMessage: String = "",
    val timeStamp: Date? = null,
    val inviter: Inviter = Inviter(),
    val invitee: Invitee = Invitee()
) {
    data class Inviter(
        val uid: String = "", val profileImageUrl: String = "", val nickName: String = ""
    )

    data class Invitee(
        val uid: String = "", val profileImageUrl: String = "", val nickName: String = ""
    )
}
