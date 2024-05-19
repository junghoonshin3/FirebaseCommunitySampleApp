package kr.sjh.domain.usecase.board

import kr.sjh.data.repository.BoardRepository
import javax.inject.Inject

class ReadPostsUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke() = boardRepository.readPosts()
}