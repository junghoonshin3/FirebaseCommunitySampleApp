package kr.sjh.domain.usecase.login.firebase

import kr.sjh.domain.repository.LoginRepository
import javax.inject.Inject

class ReadUserUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke(userId: String) = loginRepository.readUser(userId)
}