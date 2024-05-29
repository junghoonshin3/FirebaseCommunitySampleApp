package kr.sjh.domain.usecase.board

import kr.sjh.domain.repository.BoardRepository
import javax.inject.Inject

class CreatePostKeyUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke() =
        boardRepository.createPostKey()

}