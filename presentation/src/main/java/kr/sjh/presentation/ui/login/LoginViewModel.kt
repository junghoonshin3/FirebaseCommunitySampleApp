package kr.sjh.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.login.firebase.CreateUserUseCase
import kr.sjh.domain.usecase.login.firebase.DeleteUserUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.usecase.login.kakao.GetKakaoUserInfoUseCase
import kr.sjh.domain.usecase.login.kakao.LoginForKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.LogoutKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.ValidateKakaoAccessTokenUseCase
import kr.sjh.error.NotFoundUser
import kr.sjh.model.UserInfo
import javax.inject.Inject


sealed interface LoginUiState {
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val throwable: Throwable) : LoginUiState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val kakaoUserInfoUseCase: GetKakaoUserInfoUseCase,
    private val readUserUseCase: ReadUserUseCase,
    private val loginForKakaoUseCase: LoginForKakaoUseCase,
    private val validateKakaoAccessTokenUseCase: ValidateKakaoAccessTokenUseCase,
    private val logoutKakaoUseCase: LogoutKakaoUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) : ViewModel() {


    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Loading)
    val loginUiState = _loginUiState.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun loginForKakao() {
        viewModelScope.launch {
            _loginUiState.emit(LoginUiState.Loading)
            loginForKakaoUseCase()
                .mapCatching {
                    kakaoUserInfoUseCase().getOrThrow()
                }.map { user ->
                    readUserUseCase(user.id.toString())
                        .getOrElse {
                            when (it) {
                                is NotFoundUser -> {
                                    val newUser = UserInfo(
                                        user.kakaoAccount?.email,
                                        user.kakaoAccount?.profile?.nickname,
                                        user.id.toString(),
                                        user.kakaoAccount?.profile?.profileImageUrl
                                    )
                                    createUserUseCase(newUser).getOrThrow()
                                }

                                else -> {
                                    it
                                }
                            }
                        }
                }.mapCatching {
                    when (it) {
                        is UserInfo -> {
                            _loginUiState.emit(LoginUiState.Success)
                            _userInfo.value = it
                        }
                    }
                }
                .onFailure {
                    _loginUiState.emit(LoginUiState.Error(it))
                }
        }
    }

    fun onAutoLoginCheck() {
        viewModelScope.launch {
            _loginUiState.emit(LoginUiState.Loading)
            validateKakaoAccessTokenUseCase()
                .mapCatching {
                    kakaoUserInfoUseCase().getOrThrow()
                }.mapCatching { user ->
                    readUserUseCase(user.id.toString()).getOrThrow()
                }.onSuccess { userInfo ->
                    _loginUiState.emit(
                        LoginUiState.Success
                    )
                    _userInfo.value = userInfo
                }
                .onFailure {
                    it.printStackTrace()
                    _loginUiState.emit(
                        LoginUiState.Error(
                            throwable = it,
                        )
                    )
                }
        }
    }
}
