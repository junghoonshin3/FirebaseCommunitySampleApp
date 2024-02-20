package kr.sjh.presentation.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.presentation.navigation.RootScreen
import kr.sjh.presentation.utill.LoginManager
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginManager: LoginManager
) : ViewModel() {

    private val _isLogin = MutableStateFlow(false)
    val isLogin = _isLogin.asStateFlow()

    private val _startScreenName = MutableStateFlow<RootScreen?>(null)
    val startScreenName = _startScreenName.asStateFlow()

    init {
        invalidateToken()
    }

    private fun invalidateToken() {
        viewModelScope.launch(Dispatchers.IO) {
            loginManager.invalidateTokenForKakao()
                .onSuccess {
                    getLoginUserInfo {
                        _startScreenName.value = RootScreen.Main
                    }
                }
                .onFailure {
                    Log.d("sjh", "error :${it}")
                    //TODO 에러 핸들링
                    _startScreenName.value = RootScreen.Login
                }
        }
    }



    fun kakaoLogin() {
        viewModelScope.launch(Dispatchers.IO) {
            loginManager.loginForKakao()
                .onSuccess {
                    getLoginUserInfo {
                        _isLogin.value = true
                    }
                }
                .onFailure {
                    _isLogin.value = false
                }
        }
    }

    private fun getLoginUserInfo(action: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            loginManager.getLoginUserInfo()
                .onSuccess {
                    it.kakaoAccount?.email?.let {
                        Log.d("sjh", "email : ${it}")
                        action()
                    }
                }.onFailure {

                }
        }

    }
}