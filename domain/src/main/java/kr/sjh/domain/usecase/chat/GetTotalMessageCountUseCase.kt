package kr.sjh.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState


fun interface GetTotalMessageCountUseCase {
    operator fun invoke(): Flow<ResultState<Long>>
}