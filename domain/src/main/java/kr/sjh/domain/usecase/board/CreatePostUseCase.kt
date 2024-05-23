package kr.sjh.domain.usecase.board

import kr.sjh.domain.model.Post
import kr.sjh.domain.repository.BoardRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(post: Post) = boardRepository.createPost(post)

}