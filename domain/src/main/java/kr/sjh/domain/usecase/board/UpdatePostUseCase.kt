package kr.sjh.domain.usecase.board

import kr.sjh.domain.repository.BoardRepository
import kr.sjh.domain.usecase.login.model.Post
import javax.inject.Inject

class UpdatePostUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(post: Map<String, Any>) = boardRepository.updatePost(post)
}