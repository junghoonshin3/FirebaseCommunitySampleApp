package kr.sjh.data.repository

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User
import kr.sjh.domain.repository.KaKaoLoginRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KaKaoLoginRepositoryImpl @Inject constructor(
    private val context: Context,
    private val authApiClient: AuthApiClient,
    private val userApiClient: UserApiClient,
) : KaKaoLoginRepository {
    override suspend fun kaKaoLogin(): Result<OAuthToken> = runCatching {
        if (userApiClient.isKakaoTalkLoginAvailable(context)) {
            loginWithKaKaoTalk()
        } else {
            loginWithKaKaoAccount()
        }
    }.onFailure {
        // 카카오톡 로그인 시도시 로그인 된 계정이 없는경우 카카오 계정으로 로그인 시도
        if (it is AuthError) {
            loginWithKaKaoAccount()
        }
    }

    /*카카오 로그인 토큰 유효성 검사*/
    override suspend fun kaKaoTokenExist(): Result<AccessTokenInfo> = runCatching {
        suspendCoroutine { continuation ->
            // 토큰을 가지고 있더라도 사용자가 로그인 상태임을 보장할수 없음
            if (authApiClient.hasToken()) {
                userApiClient.accessTokenInfo { tokenInfo, error ->
                    Log.d("sjh", "tokeninfo : $tokenInfo, error : $error")
                    if (error != null) {
                        continuation.resumeWithException(error)
                    } else if (tokenInfo != null) {
                        //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                        continuation.resume(tokenInfo)
                    }
                }
            } else {
                // 토큰이 없는 경우 (레퍼런스 참고)
                // https://developers.kakao.com/sdk/reference/android-rx/release/kakao-open-android-docs/com.kakao.sdk.common.model/-client-error-cause/index.html#456161486%2FClasslikes%2F629633896
                continuation.resumeWithException(
                    ClientError(
                        ClientErrorCause.TokenNotFound,
                        msg = "Access token not found. You must login first."
                    )
                )
            }
        }
    }.onFailure { error ->
        if (error is KakaoSdkError && error.isInvalidTokenError()) {
            //로그인 필요
            kaKaoLogin()
        } else {
            //기타 에러
        }
    }

    //카카오 로그아웃
    override suspend fun kaKaoLogOut(): Result<Unit> = runCatching {
        suspendCoroutine { continuation ->
            userApiClient.logout { error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else {
                    continuation.resume(Unit)
                }
            }
        }

    }

    override suspend fun kaKaoMe(): Result<User> = runCatching {
        suspendCoroutine { continuation ->
            userApiClient.me { user, error ->
                if (error != null) {
                    Log.e("sjh", "사용자 정보 요청 실패", error)
                    continuation.resumeWithException(error)
                } else if (user != null) {
                    Log.d(
                        "sjh", "사용자 정보 요청 성공" +
                                "\n회원번호: ${user.id}" +
                                "\n이메일: ${user.kakaoAccount?.email}" +
                                "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                    )
                    continuation.resume(user)
                }
            }
        }
    }


    //카카오톡 로그인 시도
    private suspend fun loginWithKaKaoTalk(): OAuthToken = suspendCoroutine { continuation ->
        userApiClient.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                continuation.resumeWithException(error)
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    return@loginWithKakaoTalk
                }
            } else if (token != null) {
                continuation.resume(token)
            }
        }
    }

    //카카오 계정으로 로그인 시도
    private suspend fun loginWithKaKaoAccount(): OAuthToken = suspendCoroutine { continuation ->
        // 카카오 계정으로 로그인
        userApiClient.loginWithKakaoAccount(context) { token, error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else if (token != null) {
                continuation.resume(token)
            }
        }
    }


}