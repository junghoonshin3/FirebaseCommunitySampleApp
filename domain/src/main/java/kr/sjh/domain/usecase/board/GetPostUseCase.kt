package kr.sjh.domain.usecase.board

import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.model.UserModel

fun interface GetPostUseCase {
    operator fun invoke(postKey: String): Flow<ResultState<Pair<PostModel, UserModel>>>
}