package kr.sjh.domain.repository

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User

interface KaKaoLoginRepository {
    suspend fun kaKaoLogin(context: Context): Result<OAuthToken>
    suspend fun kaKaoTokenExist(context: Context): Result<AccessTokenInfo>
    suspend fun kaKaoLogOut(): Result<Unit>
    suspend fun kaKaoMe(): Result<User>
}