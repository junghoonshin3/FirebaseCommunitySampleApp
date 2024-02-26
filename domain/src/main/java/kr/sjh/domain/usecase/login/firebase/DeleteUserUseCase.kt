package kr.sjh.domain.usecase.login.firebase

import kr.sjh.domain.repository.LoginRepository
import javax.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke(id: String?) = loginRepository.deleteUser(id)
}