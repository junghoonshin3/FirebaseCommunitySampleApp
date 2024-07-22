package kr.sjh.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.exception.FirebaseAuthCustomException
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import kr.sjh.domain.usecase.user.ExistUserUseCase
import kr.sjh.domain.usecase.user.GetUserUseCase
import javax.inject.Inject

sealed class AuthUiState {
    data object Init : AuthUiState()

    data object AuthToLogin : AuthUiState()

    data object AuthToLoginDetail : AuthUiState()

    data object AuthToMain : AuthUiState()

    data class Error(val throwable: Throwable) : AuthUiState()

}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAuthCurrentUserUseCase: GetAuthCurrentUserUseCase,
    private val existUserUseCase: ExistUserUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Init)
    val authState = _authState.asStateFlow()

    init {
        isUserLoggedIn()
    }

    private fun isUserLoggedIn() {
        viewModelScope.launch {
            val uid = getAuthCurrentUserUseCase()?.uid
            if (uid != null) {
                existUserUseCase(uid).collect { result ->
                    when (result) {
                        is ResultState.Failure -> {
                            result.throwable.printStackTrace()
                            when (result.throwable) {
                                is FirebaseAuthCustomException.UserNotFoundInUsers -> {
                                    _authState.value = AuthUiState.AuthToLoginDetail
                                }
                            }
                        }

                        ResultState.Loading -> {

                        }

                        is ResultState.Success -> {
                            if (result.data) {
                                _authState.value = AuthUiState.AuthToMain
                            } else {
                                _authState.value = AuthUiState.AuthToLoginDetail
                            }

                        }
                    }
                }
            } else {
                _authState.value = AuthUiState.AuthToLogin
            }
        }
    }
}