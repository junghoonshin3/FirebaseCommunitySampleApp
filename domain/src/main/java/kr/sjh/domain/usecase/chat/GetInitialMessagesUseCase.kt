package kr.sjh.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel

fun interface GetInitialMessagesUseCase {
    operator fun invoke(
        roomId: String, limit: Long
    ): Flow<ResultState<ChatMessageModel>>
}