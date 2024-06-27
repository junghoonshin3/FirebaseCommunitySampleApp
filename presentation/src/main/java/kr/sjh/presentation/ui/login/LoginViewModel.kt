package kr.sjh.presentation.ui.login

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.CredentialModel
import kr.sjh.domain.usecase.auth.firebase.AuthSignInUseCase
import kr.sjh.presentation.helper.GoogleLoginHelper
import javax.inject.Inject

sealed class LoginUiState {
    data object LoginToDetail : LoginUiState()
    data object LoginToMain : LoginUiState()
    data class Error(val throwable: Throwable) : LoginUiState()
    data object Init : LoginUiState()

    data object Loading : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: AuthSignInUseCase,
    private val loginHelper: GoogleLoginHelper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Init)
    val loginUiState = _loginUiState.asStateFlow()

    fun signIn(activityContext: Context) {
        viewModelScope.launch {
            try {
                val googleIdTokenCredential =
                    loginHelper.requestGoogleLogin(activityContext = activityContext)
                googleIdTokenCredential?.let {
                    signInUseCase(
                        CredentialModel(
                            googleIdTokenCredential.idToken
                        )
                    ).collect {
                        when (it) {
                            is ResultState.Failure -> {
                                it.throwable.printStackTrace()
                                _loginUiState.value = LoginUiState.Error(it.throwable)
                            }

                            ResultState.Loading -> {
                                _loginUiState.value = LoginUiState.Loading
                            }

                            is ResultState.Success -> {
                                if (it.data) {
                                    _loginUiState.value = LoginUiState.LoginToMain
                                } else {
                                    _loginUiState.value = LoginUiState.LoginToDetail
                                }

                            }
                        }
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}