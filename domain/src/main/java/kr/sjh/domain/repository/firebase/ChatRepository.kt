package kr.sjh.domain.repository.firebase

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.model.ChatRoomModel

interface ChatRepository {
    fun getInitialMessages(roomId: String, limit: Long): Flow<ResultState<ChatMessageModel>>
    fun getNextMessages(
        roomId: String, limit: Long, fromTime: Long
    ): Flow<ResultState<List<ChatMessageModel>>>

    suspend fun sendMessage(chatMessageModel: ChatMessageModel): Flow<ResultState<Unit>>
    fun getChatRooms(uid: String): Flow<ResultState<List<ChatRoomModel>>>
}