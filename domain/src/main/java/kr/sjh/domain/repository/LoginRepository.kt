package kr.sjh.domain.repository

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User
import kr.sjh.domain.usecase.login.model.UserInfo

interface LoginRepository {
    suspend fun signInForKakao(): Result<OAuthToken>

    suspend fun validateTokenForKakao(): Result<AccessTokenInfo>

    suspend fun userInfoForKakao(): Result<User>

    suspend fun createUser(user: UserInfo): Result<Boolean>

    suspend fun readUser(id: String?): Result<UserInfo>

    suspend fun deleteUser(id: String?): Result<Boolean>
}