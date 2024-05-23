package kr.sjh.domain.repository

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User

interface KaKaoLoginRepository {
    suspend fun kaKaoLogin(): Result<OAuthToken>
    suspend fun kaKaoTokenExist(): Result<AccessTokenInfo>
    suspend fun kaKaoLogOut(): Result<Unit>

    suspend fun kaKaoMe(): Result<User>
}