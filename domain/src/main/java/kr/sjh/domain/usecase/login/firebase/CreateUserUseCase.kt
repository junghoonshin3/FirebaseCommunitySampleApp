package kr.sjh.domain.usecase.login.firebase

import kr.sjh.data.repository.LoginRepository
import kr.sjh.model.UserInfo
import javax.inject.Inject

class CreateUserUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke(user: UserInfo) = loginRepository.createUser(user)

}