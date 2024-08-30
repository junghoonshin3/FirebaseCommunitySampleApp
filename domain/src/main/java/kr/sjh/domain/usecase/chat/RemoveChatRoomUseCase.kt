package kr.sjh.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatRoomModel

fun interface RemoveChatRoomUseCase {
    operator fun invoke(chatRoom: ChatRoomModel): Flow<ResultState<Unit>>
}