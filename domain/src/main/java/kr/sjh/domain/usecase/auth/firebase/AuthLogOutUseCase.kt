package kr.sjh.domain.usecase.auth.firebase


fun interface AuthLogOutUseCase {
    suspend operator fun invoke()
}