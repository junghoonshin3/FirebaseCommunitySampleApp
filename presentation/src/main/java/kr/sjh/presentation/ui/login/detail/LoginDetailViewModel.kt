package kr.sjh.presentation.ui.login.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.constant.Role
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.usecase.auth.firebase.AuthLogOutUseCase
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import kr.sjh.domain.usecase.user.SignUpUseCase
import kr.sjh.presentation.R
import javax.inject.Inject

data class LoginDetailUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val nickName: String = "",
    val profileImageUrl: String = "",
    val throwable: Throwable? = null
)

@HiltViewModel
class LoginDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val signUpUseCase: SignUpUseCase,
    getAuthCurrentUserUseCase: GetAuthCurrentUserUseCase,
    private val logOutUseCase: AuthLogOutUseCase
) : ViewModel() {

    private val authUser = getAuthCurrentUserUseCase()

    private val _loginDetailUiState = MutableStateFlow(
        LoginDetailUiState(
            nickName = savedStateHandle.get<String>("nickName") ?: authUser?.nickName!!,
            profileImageUrl = savedStateHandle.get<String>("profileImageUrl")
                ?: authUser?.profileImageUrl!!
        )
    )

    val loginDetailUiState = _loginDetailUiState.asStateFlow()

    fun signUp(context: Context) {
        viewModelScope.launch {
            signUpUseCase(
                UserModel(
                    uid = authUser?.uid ?: "",
                    email = authUser?.email ?: "",
                    profileImageUrl = loginDetailUiState.value.profileImageUrl,
                    nickName = loginDetailUiState.value.nickName,
                    role = if (authUser?.email == context.resources.getString(R.string.ADMIN_EMAIL)) {
                        Role.ADMIN
                    } else {
                        Role.USER
                    }
                )
            ).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _loginDetailUiState.update {
                            it.copy(
                                isLoading = false, isSuccess = false, throwable = result.throwable
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _loginDetailUiState.update {
                            it.copy(isLoading = true, isSuccess = false, throwable = null)
                        }
                    }

                    is ResultState.Success -> {
                        _loginDetailUiState.update {
                            it.copy(isLoading = false, isSuccess = true, throwable = null)
                        }
                    }
                }
            }
        }
    }

    fun changeNickName(nickName: String) {
        _loginDetailUiState.update {
            it.copy(
                nickName = nickName
            )
        }
        savedStateHandle["nickName"] = nickName
    }

    fun onImageEdit(profileImageUrl: String) {
        _loginDetailUiState.update {
            it.copy(
                profileImageUrl = profileImageUrl
            )
        }
        savedStateHandle["profileImageUrl"] = profileImageUrl
    }

    fun onBack() {
        viewModelScope.launch {
            logOutUseCase()
        }
    }
}
