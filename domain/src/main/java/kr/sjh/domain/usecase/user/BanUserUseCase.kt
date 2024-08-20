package kr.sjh.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState

fun interface BanUserUseCase {
    operator fun invoke(banUid: String): Flow<ResultState<Unit>>
}