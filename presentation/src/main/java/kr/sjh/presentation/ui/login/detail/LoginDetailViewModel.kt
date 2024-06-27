package kr.sjh.presentation.ui.login.detail

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import kr.sjh.domain.usecase.user.SignUpUseCase
import javax.inject.Inject

sealed interface LoginDetailUiState {
    data object LoginDetailToMain : LoginDetailUiState
    data object Loading : LoginDetailUiState
    data class Fail(val throwable: Throwable) : LoginDetailUiState
    data object Init : LoginDetailUiState
}

@HiltViewModel
class LoginDetailViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val getAuthCurrentUserUseCase: GetAuthCurrentUserUseCase
) : ViewModel() {

    private val uid by mutableStateOf(getAuthCurrentUserUseCase()?.uid ?: "")
    private val email by mutableStateOf(getAuthCurrentUserUseCase()?.email ?: "")
    var imageUrl by mutableStateOf(getAuthCurrentUserUseCase()?.profileImageUrl ?: "")
    var nickName by mutableStateOf(getAuthCurrentUserUseCase()?.nickName ?: "")

    private val _loginDetailUiState = MutableStateFlow<LoginDetailUiState>(LoginDetailUiState.Init)
    val loginDetailUiState = _loginDetailUiState.asStateFlow()


    fun signUp() {
        viewModelScope.launch {
            signUpUseCase(
                UserModel(
                    uid = uid,
                    email = email,
                    profileImageUrl = imageUrl,
                    nickName = nickName
                )
            ).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _loginDetailUiState.value = LoginDetailUiState.Fail(result.throwable)
                    }

                    ResultState.Loading -> {
                        _loginDetailUiState.value = LoginDetailUiState.Loading
                    }

                    is ResultState.Success -> {
                        _loginDetailUiState.value = LoginDetailUiState.LoginDetailToMain
                    }
                }
            }
        }
    }

    fun changeNickName(nickName: String) {
        this.nickName = nickName
    }

    fun setImageUri(imageUri: Uri) {
        this.imageUrl = imageUri.toString()
    }
}
