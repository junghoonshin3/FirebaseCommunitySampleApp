package kr.sjh.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class PostEntity(
    val writerUid: String = "",
    val postKey: String = "",
    var title: String = "",
    var content: String = "",
    var nickName: String = "",
    @ServerTimestamp val timeStamp: Timestamp? = null,
    var readCount: Int = 0,
    var likeCount: Int = 0,
    val images: List<String> = listOf(),
)