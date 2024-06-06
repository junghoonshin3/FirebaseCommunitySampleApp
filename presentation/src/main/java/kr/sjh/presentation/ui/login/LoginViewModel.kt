package kr.sjh.presentation.ui.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.login.firebase.CreateUserUseCase
import kr.sjh.domain.usecase.login.firebase.DeleteUserUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.usecase.login.kakao.KaKaoLogOutUseCase
import kr.sjh.domain.usecase.login.kakao.KaKaoLoginUseCase
import kr.sjh.domain.usecase.login.kakao.KaKaoMeUseCase
import kr.sjh.domain.usecase.login.kakao.KaKaoExistAccessToken
import kr.sjh.domain.model.UserInfo
import kr.sjh.presentation.utill.toUserInfo
import javax.inject.Inject

sealed interface LoginUiState {

    data object Init : LoginUiState
    data object Loading : LoginUiState

    data object Success : LoginUiState

    data class Error(val throwable: Throwable) : LoginUiState

}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val kaKaLoginUseCase: KaKaoLoginUseCase,
    private val kaKaoMeUseCase: KaKaoMeUseCase,
    private val kaKaoLogOutUseCase: KaKaoLogOutUseCase,
    private val kaKaoExistAccessToken: KaKaoExistAccessToken,
    private val readUserUseCase: ReadUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) : ViewModel() {

    var userInfo by mutableStateOf<UserInfo?>(null)
    fun kaKaoLogin(context: Context, onResult: (UserInfo?, Throwable?) -> Unit) {
        viewModelScope.launch {
            kaKaLoginUseCase(context).mapCatching { authToken ->
                //카카오 로그인 성공 및 카카오 사용자 정보 가져오기
                kaKaoMeUseCase().getOrThrow()
            }.mapCatching { user ->
                //파이어 베이스 db에서 계정정보 읽어오기
                userInfo = user.toUserInfo()
                readUserUseCase(user.id.toString()).getOrThrow()
            }.onSuccess {
                // 성공시 상태변경 및 유저정보
                userInfo = it
                onResult(it, null)
            }
                .onFailure {
                    onResult(null, it)
                }
        }
    }

    fun hasToken(context: Context, onResult: (UserInfo?, Throwable?) -> Unit) {
        viewModelScope.launch {
            kaKaoExistAccessToken(context).mapCatching {
                //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
                Log.d("sjh", "authToken : ${it.id}")
                //카카오 계정정보 가져오기
                kaKaoMeUseCase().getOrThrow()
            }.mapCatching { user ->
                readUserUseCase(user.id.toString()).getOrThrow()
            }.onSuccess {
                onResult(it, null)
            }.onFailure {
                onResult(null, it)
            }
        }
    }

    fun createUser(nickName: String, onResult: (UserInfo?, Throwable?) -> Unit) {
        viewModelScope.launch {
            userInfo?.let {
                createUserUseCase(it.apply {
                    this.nickName = nickName
                }).onSuccess {
                    userInfo = it
                    onResult(it, null)
                }.onFailure {
                    onResult(null, it)
                }
            }

        }
    }

    fun updateUser() {

    }

}
