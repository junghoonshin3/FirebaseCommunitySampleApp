package kr.sjh.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.error.NotFoundUser
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.kakao.GetKakaoUserInfoUseCase
import kr.sjh.domain.usecase.login.kakao.ValidateKakaoAccessTokenUseCase
import kr.sjh.domain.usecase.login.model.UserInfo
import kr.sjh.presentation.navigation.Graph
import javax.inject.Inject


sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val userInfo: UserInfo) : MainUiState
    data class Error(val throwable: Throwable) : MainUiState
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val kakaoUserInfoUseCase: GetKakaoUserInfoUseCase,
    private val validateKakaoAccessTokenUseCase: ValidateKakaoAccessTokenUseCase,
    private val readUserUseCase: ReadUserUseCase
) : ViewModel() {

    private val _mainUiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val mainUiState = _mainUiState.asStateFlow()

    init {
        onAutoLoginCheck()
    }

    private fun onAutoLoginCheck() {
        viewModelScope.launch {
            _mainUiState.emit(MainUiState.Loading)
            validateKakaoAccessTokenUseCase()
                .mapCatching {
                    kakaoUserInfoUseCase().getOrThrow()
                }.mapCatching { user ->
                    readUserUseCase(user.id.toString()).getOrThrow()
                }.onSuccess { userInfo ->
                    _mainUiState.emit(
                        MainUiState.Success(
                            userInfo = userInfo
                        )
                    )
                }
                .onFailure {
                    it.printStackTrace()
                    _mainUiState.emit(
                        MainUiState.Error(
                            throwable = it,
                        )
                    )
                }
        }

    }

}

