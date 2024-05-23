package kr.sjh.domain.usecase.board

import kr.sjh.domain.repository.BoardRepository
import javax.inject.Inject


class DeletePostUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(postKey: String) = boardRepository.deletePost(postKey)
}