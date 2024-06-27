package kr.sjh.domain.model

import androidx.compose.runtime.Stable
import java.io.Serializable
import java.net.URI
import java.util.Date
import java.util.UUID

@Stable
data class PostModel(
    val writerUid: String = "",
    val postKey: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val nickName: String = "",
    val timeStamp: Long = Date().time,
    val readCount: Int = 0,
    val likeCount: Int = 0,
    val images: List<String> = listOf()
)
