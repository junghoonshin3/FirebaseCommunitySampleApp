package kr.sjh.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class PostEntity(
    val writerUid: String = "",
    val postKey: String = "",
    var title: String = "",
    var content: String = "",
    var nickName: String = "",
    val timeStamp: Long = Date().time,
    var readCount: Int = 0,
    var likeCount: Int = 0,
    val images: List<String> = listOf()
)