package kr.sjh.domain.usecase.board

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kr.sjh.domain.repository.BoardRepository
import javax.inject.Inject

class ReadPostsUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke() = boardRepository.readPosts()
}