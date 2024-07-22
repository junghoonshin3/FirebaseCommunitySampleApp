package kr.sjh.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel

fun interface GetPostsUseCase {
    suspend operator fun invoke(): Flow<ResultState<List<PostModel>>>
}