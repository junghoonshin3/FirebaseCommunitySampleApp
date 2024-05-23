package kr.sjh.domain.usecase.login.firebase

import kr.sjh.domain.model.UserInfo
import kr.sjh.domain.repository.LoginRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke(user: UserInfo) = loginRepository.updateUser(user)
}