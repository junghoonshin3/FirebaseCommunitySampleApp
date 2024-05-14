package kr.sjh.domain.usecase.board

import kr.sjh.domain.repository.BoardRepository
import javax.inject.Inject

class ReadPostUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(postKey: String) = boardRepository.readPost(postKey)
}