package kr.sjh.presentation.ui.chat

data class ChatMessage(
    val profileImageUrl: String,
    val message: String,
    val createAt: Long,
)