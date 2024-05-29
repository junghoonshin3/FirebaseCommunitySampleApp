package kr.sjh.domain.usecase.board

import android.net.Uri
import kr.sjh.domain.repository.BoardRepository
import javax.inject.Inject

class UploadMultiImageUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(postKey: String, images: List<Uri>) =
        boardRepository.uploadImages(postKey, images)

}