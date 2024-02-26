package kr.sjh.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.kakao.GetKakaoUserInfoUseCase
import kr.sjh.domain.usecase.login.kakao.ValidateKakaoAccessTokenUseCase
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.navigation.RootScreen
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val kakaoUserInfoUseCase: GetKakaoUserInfoUseCase,
    private val validateKakaoAccessTokenUseCase: ValidateKakaoAccessTokenUseCase,
    private val readUserUseCase: ReadUserUseCase
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _startScreenName = MutableStateFlow<RootScreen?>(null)
    val startScreenName = _startScreenName.asStateFlow()

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
}