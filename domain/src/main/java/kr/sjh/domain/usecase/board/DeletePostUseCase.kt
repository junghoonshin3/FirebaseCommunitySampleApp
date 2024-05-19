package kr.sjh.domain.usecase.board

import kr.sjh.data.repository.BoardRepository
import kr.sjh.model.Post
import javax.inject.Inject


class DeletePostUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(post: Post) = boardRepository.deletePost(post)
}