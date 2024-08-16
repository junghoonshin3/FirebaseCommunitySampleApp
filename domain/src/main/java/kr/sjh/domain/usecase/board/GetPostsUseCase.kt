package kr.sjh.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel

fun interface GetPostsUseCase {
    operator fun invoke(size: Long, lastTime: Long?): Flow<ResultState<List<PostModel>>>
}