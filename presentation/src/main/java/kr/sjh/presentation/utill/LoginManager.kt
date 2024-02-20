package kr.sjh.presentation.utill

import android.content.Context
import android.util.Log
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.kakao.sdk.user.model.User
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kr.sjh.presentation.navigation.RootScreen
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LoginManager(private val context: Context) {

    /**
     * 토큰 조회
     */
    suspend fun invalidateTokenForKakao(): Result<AccessTokenInfo> = runCatching {
        if (AuthApiClient.Companion.instance.hasToken()) {
            try {
                accessTokenInfoForKakao()
            } catch (error: Exception) {
                throw error
            }
        } else {
            //로그인 필요
            throw Exception(RuntimeException("Can't Receive Kakao Access Token"))
        }
    }


    private suspend fun accessTokenInfoForKakao(): AccessTokenInfo =
        suspendCoroutine { continuation ->
            UserApiClient.Companion.instance.accessTokenInfo { info, error ->
                continuation.resumeTokenOrException(info, error)
            }
        }

    /**
     * 카카오톡 설치 여부에 따라서 설치 되어있으면 카카오톡 로그인을 시도한다.
     * 미설치 시 카카오 계정 로그인을 시도한다.
     * 카카오톡 로그인에 실패하면 사용자가 의도적으로 로그인 취소한 경우를 제외하고는 카카오 계정 로그인을 서브로 실행한다.
     */
    suspend fun loginForKakao(): Result<OAuthToken> = runCatching {
        if (UserApiClient.Companion.instance.isKakaoTalkLoginAvailable(context)) {
            try {
                loginWithKakaoTalk()
            } catch (error: Throwable) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    throw error
                } else {
                    loginWithKakaoAccount()
                }
            }
        } else {
            loginWithKakaoAccount()
        }
    }

    private suspend fun loginWithKakaoTalk(): OAuthToken = suspendCoroutine { continuation ->
        // 카카오톡으로 로그인
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            continuation.resumeTokenOrException(token, error)
        }
    }

    private suspend fun loginWithKakaoAccount(): OAuthToken = suspendCoroutine { continuation ->
        // 카카오톡으로 로그인
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            continuation.resumeTokenOrException(token, error)
        }
    }

    suspend fun getLoginUserInfo(): Result<User> = runCatching {
        try {
            bringMe()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun bringMe(): User = suspendCoroutine { continuation ->
        UserApiClient.Companion.instance.me { user, error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else {
                user?.let {
                    continuation.resume(user)
                } ?: let {
                    continuation.resumeWithException(Exception("User does not exist"))
                }

            }
        }
    }

    private fun Continuation<OAuthToken>.resumeTokenOrException(
        token: OAuthToken?,
        error: Throwable?
    ) {
        if (error != null) {
            resumeWithException(error)
        } else if (token != null) {
            resume(token)
        } else {
            resumeWithException(RuntimeException("Can't Receive Kakao Access Token"))
        }
    }

    private fun Continuation<AccessTokenInfo>.resumeTokenOrException(
        token: AccessTokenInfo?,
        error: Throwable?
    ) {
        if (error != null) {
            if (error is KakaoSdkError && error.isInvalidTokenError()) {
                //로그인 필요
                resumeWithException(Exception("InvalidTokenError"))
            } else {
                resumeWithException(error)
            }
        } else if (token != null) {
            resume(token)
        } else {
            resumeWithException(RuntimeException("Can't Receive Kakao Access Token"))
        }
    }
}
