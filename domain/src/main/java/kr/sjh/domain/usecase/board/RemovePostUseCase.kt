package kr.sjh.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState

fun interface RemovePostUseCase {
    suspend operator fun invoke(postKey: String): Flow<ResultState<Unit>>
}