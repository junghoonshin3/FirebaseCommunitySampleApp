package kr.sjh.presentation.ui

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.sjh.domain.usecase.login.firebase.CreateUserUseCase
import kr.sjh.domain.usecase.login.firebase.DeleteUserUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.usecase.login.kakao.GetKakaoUserInfoUseCase
import kr.sjh.domain.usecase.login.kakao.LoginForKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.LogoutKakaoUseCase
import kr.sjh.domain.usecase.login.kakao.ValidateKakaoAccessTokenUseCase
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.navigation.RootScreen
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val kakaoUserInfoUseCase: GetKakaoUserInfoUseCase,
    private val validateKakaoAccessTokenUseCase: ValidateKakaoAccessTokenUseCase,
    private val readUserUseCase: ReadUserUseCase,
    private val loginForKakaoUseCase: LoginForKakaoUseCase,
    private val logoutKakaoUseCase: LogoutKakaoUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _startScreenName = MutableStateFlow<RootScreen?>(null)
    val startScreenName = _startScreenName.asStateFlow()

    private val _isLogin = MutableStateFlow(false)
    val isLogin = _isLogin.asStateFlow()


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
                                    it.printStackTrace()
                                    _userInfo.value = null
                                    _startScreenName.value = RootScreen.Login
                                }
                        }
                        .onFailure {
                            it.printStackTrace()
                            _userInfo.value = null
                            _startScreenName.value = RootScreen.Login
                        }
                }
                .onFailure {
                    it.printStackTrace()
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
                                    if (it is Resources.NotFoundException) {
                                        val newUser = UserInfo(
                                            user.kakaoAccount?.email,
                                            user.kakaoAccount?.profile?.nickname,
                                            user.id.toString(),
                                            user.kakaoAccount?.profile?.profileImageUrl
                                        )
                                        createUserUseCase(
                                            newUser
                                        ).onSuccess {
                                            _userInfo.value = newUser
                                            _isLogin.value = it
                                        }.onFailure {
                                            _userInfo.value = null
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

    fun logOut(callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            logoutKakaoUseCase().onSuccess {
                _userInfo.value = null
                _isLogin.value = false
                withContext(Dispatchers.Main) {
                    callBack()
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun updateUserInfo(user: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserUseCase(user)
                .onSuccess {
                    _userInfo.update { user }
                }
                .onFailure {

                }
        }
    }
}