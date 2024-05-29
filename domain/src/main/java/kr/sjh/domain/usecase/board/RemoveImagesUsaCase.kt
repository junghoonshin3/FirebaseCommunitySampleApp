package kr.sjh.domain.usecase.board

import android.net.Uri
import kr.sjh.domain.repository.BoardRepository
import javax.inject.Inject


class RemoveImagesUsaCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(postKey: String) =
        boardRepository.removeImages(postKey)

}