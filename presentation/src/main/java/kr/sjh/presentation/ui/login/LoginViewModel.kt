package kr.sjh.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.error.NotFoundUser
import kr.sjh.domain.usecase.login.firebase.CreateUserUseCase
import kr.sjh.domain.usecase.login.firebase.DeleteUserUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.usecase.login.kakao.GetKakaoUserInfoUseCase
import kr.sjh.domain.usecase.login.kakao.LoginForKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.LogoutKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.ValidateKakaoAccessTokenUseCase
import kr.sjh.domain.usecase.login.model.UserInfo
import javax.inject.Inject


sealed interface LoginUiState {
    data object Loading : LoginUiState
    data class Success(val userInfo: UserInfo) : LoginUiState
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

    private val _loginUiState = MutableSharedFlow<LoginUiState>()
    val loginUiState = _loginUiState.asSharedFlow()
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
                            Log.d("sjh", "user :${it.nickName}")
                            _loginUiState.emit(LoginUiState.Success(it))
                        }
                    }
                }
                .onFailure {
                    _loginUiState.emit(LoginUiState.Error(it))
                }
        }
    }
}
