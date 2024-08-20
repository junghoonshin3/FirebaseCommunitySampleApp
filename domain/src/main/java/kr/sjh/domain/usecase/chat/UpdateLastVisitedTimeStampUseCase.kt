package kr.sjh.domain.usecase.chat

fun interface UpdateLastVisitedTimeStampUseCase {
    suspend operator fun invoke(roomId: String)

}