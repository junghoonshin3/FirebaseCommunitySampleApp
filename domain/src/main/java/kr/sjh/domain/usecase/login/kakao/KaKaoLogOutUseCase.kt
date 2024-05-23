package kr.sjh.domain.usecase.login.kakao

import kr.sjh.domain.repository.KaKaoLoginRepository
import javax.inject.Inject

class KaKaoLogOutUseCase @Inject constructor(
    private val kaKaoRepository: KaKaoLoginRepository,
) {
    suspend operator fun invoke() = kaKaoRepository.kaKaoLogOut()
}