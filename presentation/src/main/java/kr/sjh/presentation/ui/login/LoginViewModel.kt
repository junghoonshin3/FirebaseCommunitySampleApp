package kr.sjh.presentation.ui.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.AuthUserModel
import kr.sjh.domain.model.CredentialModel
import kr.sjh.domain.usecase.auth.firebase.AuthSignInUseCase
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import kr.sjh.presentation.helper.GoogleLoginHelper
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false, val destination: String = "", val throwable: Throwable? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: AuthSignInUseCase,
    private val loginHelper: GoogleLoginHelper,
) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState = _loginUiState.asStateFlow()

    fun signIn(activityContext: Context) {
        viewModelScope.launch {
            try {
                val googleIdTokenCredential =
                    loginHelper.requestGoogleLogin(activityContext = activityContext).getOrThrow()

                googleIdTokenCredential?.let {
                    signInUseCase(
                        CredentialModel(
                            googleIdTokenCredential.idToken
                        )
                    ).collect { result ->
                        when (result) {
                            is ResultState.Failure -> {
                                _loginUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        destination = "",
                                        throwable = it.throwable
                                    )
                                }
                            }

                            ResultState.Loading -> {
                                _loginUiState.update {
                                    it.copy(
                                        isLoading = true, destination = "", throwable = null
                                    )
                                }
                            }

                            is ResultState.Success -> {
                                _loginUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        destination = if (result.data) "loginToMain" else "loginToDetail",
                                        throwable = null,
                                    )
                                }
                            }
                        }
                    }
                }

            } catch (e: GetCredentialCancellationException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}