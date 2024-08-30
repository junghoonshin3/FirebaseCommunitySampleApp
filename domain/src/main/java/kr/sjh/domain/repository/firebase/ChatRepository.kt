package kr.sjh.domain.repository.firebase

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.model.ChatRoomModel

interface ChatRepository {
    fun getInitialMessages(
        roomId: String, size: Long
    ): Flow<ResultState<List<ChatMessageModel>>>

    fun getNextMessages(
        roomId: String, size: Long, fromTime: Long
    ): Flow<ResultState<List<ChatMessageModel>>>

    suspend fun sendMessage(message: ChatMessageModel): Flow<ResultState<Unit>>
    fun getChatRooms(): Flow<ResultState<List<ChatRoomModel>>>
    suspend fun updateLastVisitedTimeStamp(roomId: String)
    fun getTotalMessageCount(): Flow<ResultState<Long>>
    fun removeChatRoom(chatRoom: ChatRoomModel): Flow<ResultState<Unit>>
}