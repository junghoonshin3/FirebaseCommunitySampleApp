package kr.sjh.presentation.ui.login

import android.content.res.Resources.NotFoundException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.login.firebase.CreateUserUseCase
import kr.sjh.domain.usecase.login.firebase.DeleteUserUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.kakao.GetKakaoUserInfoUseCase
import kr.sjh.domain.usecase.login.kakao.LoginForKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.LogoutKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.ValidateKakaoAccessTokenUseCase
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.navigation.RootScreen
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val kakaoUserInfoUseCase: GetKakaoUserInfoUseCase,
    private val loginForKakaoUseCase: LoginForKakaoUseCase,
    private val logoutKakaoUseCase: LogoutKakaoUseCase,
    private val validateKakaoAccessTokenUseCase: ValidateKakaoAccessTokenUseCase,
    private val readUserUseCase: ReadUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val createUserUseCase: CreateUserUseCase
) : ViewModel() {

    private val _isLogin = MutableStateFlow(false)
    val isLogin = _isLogin.asStateFlow()

    private val _startScreenName = MutableStateFlow<RootScreen?>(null)
    val startScreenName = _startScreenName.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    init {
        validateToken()
    }

    private fun validateToken() {
        viewModelScope.launch(Dispatchers.IO) {
            validateKakaoAccessTokenUseCase()
                .onSuccess {
                    kakaoUserInfoUseCase()
                        .onSuccess { user ->
                            readUserUseCase(user.id.toString())
                                .onSuccess {
                                    _userInfo.value = it
                                    _startScreenName.value = RootScreen.Main
                                }.onFailure {
                                    _userInfo.value = null
                                    _startScreenName.value = RootScreen.Login
                                }
                        }
                        .onFailure {
                            _userInfo.value = null
                            _startScreenName.value = RootScreen.Login
                        }
                }
                .onFailure {
                    _userInfo.value = null
                    _startScreenName.value = RootScreen.Login
                }
        }
    }

    fun loginForKakao() {
        viewModelScope.launch(Dispatchers.IO) {
            loginForKakaoUseCase()
                .onSuccess {
                    kakaoUserInfoUseCase()
                        .onSuccess { user ->
                            readUserUseCase(user.id.toString())
                                .onSuccess {
                                    _userInfo.value = it
                                    _isLogin.value = true
                                }
                                .onFailure {
                                    if (it is NotFoundException) {
                                        createUserUseCase(
                                            UserInfo(
                                                user.kakaoAccount?.email,
                                                user.kakaoAccount?.profile?.nickname,
                                                user.id.toString(),
                                                user.kakaoAccount?.profile?.profileImageUrl
                                            )
                                        ).onSuccess {
                                            _isLogin.value = it
                                        }.onFailure {
                                            _isLogin.value = false
                                        }
                                    }
                                }
                        }
                        .onFailure {
                            _userInfo.value = null
                            _isLogin.value = false
                        }
                }
                .onFailure {
                    _userInfo.value = null
                    _isLogin.value = false
                }
        }
    }
}