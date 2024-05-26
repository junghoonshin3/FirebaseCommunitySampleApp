package kr.sjh.domain.usecase.login.kakao

import android.content.Context
import kr.sjh.domain.repository.KaKaoLoginRepository
import javax.inject.Inject


class KaKaoExistAccessToken @Inject constructor(
    private val kaKaoLoginRepository: KaKaoLoginRepository
) {
    suspend operator fun invoke(context: Context) = kaKaoLoginRepository.kaKaoTokenExist(context)
}