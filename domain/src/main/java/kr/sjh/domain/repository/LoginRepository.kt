package kr.sjh.domain.repository

import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.flow.Flow
import kr.sjh.domain.usecase.login.model.UserInfo

interface LoginRepository {
    suspend fun loginForKakao(): Result<OAuthToken>

    suspend fun validateTokenForKakao(): Result<AccessTokenInfo>

    suspend fun userInfoForKakao(): Result<User>

    suspend fun createUser(user: UserInfo): Result<UserInfo>

    suspend fun readUser(id: String): Result<UserInfo>

    suspend fun deleteUser(id: String?): Result<Boolean>

    suspend fun updateUser(user: UserInfo): Result<Boolean>

    suspend fun logOutUser(): Result<Boolean>
}