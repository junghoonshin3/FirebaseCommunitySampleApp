package kr.sjh.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel

fun interface SendMessageUseCase {
    suspend operator fun invoke(messageModel: ChatMessageModel): Flow<ResultState<Unit>>
}