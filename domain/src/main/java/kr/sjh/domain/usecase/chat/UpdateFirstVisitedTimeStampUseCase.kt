package kr.sjh.domain.usecase.chat

fun interface UpdateFirstVisitedTimeStampUseCase {
    suspend operator fun invoke(roomId: String)

}