package kr.sjh.domain.usecase.board

import kr.sjh.data.repository.BoardRepository
import javax.inject.Inject

class ReadPostUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(postKey: String) = boardRepository.readPost(postKey)
}