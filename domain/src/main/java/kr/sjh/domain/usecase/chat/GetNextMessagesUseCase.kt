package kr.sjh.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel

fun interface GetNextMessagesUseCase {
    operator fun invoke(
        roomId: String,
        limit: Long,
        fromTime: Long
    ): Flow<ResultState<List<ChatMessageModel>>>
}