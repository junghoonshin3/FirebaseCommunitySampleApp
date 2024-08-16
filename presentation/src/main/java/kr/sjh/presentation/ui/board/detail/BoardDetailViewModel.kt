package kr.sjh.presentation.ui.board.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.usecase.board.GetPostUseCase
import kr.sjh.domain.usecase.board.RemovePostUseCase
import kr.sjh.domain.usecase.user.BanUserUseCase
import javax.inject.Inject

data class DetailUiState(
    val loading: Boolean = false,
    val post: PostModel = PostModel(),
    val writerUser: UserModel = UserModel(),
    val bottomSheetShow: Boolean = false,
    val throwable: Throwable? = null
)


@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    private val removePostUseCase: RemovePostUseCase,
    private val banUserUseCase: BanUserUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val postKey = savedStateHandle.get<String>("postKey").orEmpty()

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getPost()
    }

    fun setBottomSheetVisible() {
        _uiState.update {
            it.copy(
                bottomSheetShow = !it.bottomSheetShow
            )
        }
    }

    private fun getPost() {
        viewModelScope.launch {
            getPostUseCase(postKey).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _uiState.update {
                            it.copy(
                                loading = false, throwable = result.throwable
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _uiState.update {
                            it.copy(
                                loading = true
                            )
                        }
                    }

                    is ResultState.Success -> {
                        _uiState.update {
                            it.copy(
                                loading = false,
                                post = result.data.first,
                                writerUser = result.data.second
                            )
                        }
                    }
                }
            }
        }
    }

    fun deletePost(onSuccess: () -> Unit) {
        viewModelScope.launch {
            removePostUseCase(postKey).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _uiState.update {
                            it.copy(
                                loading = false, throwable = it.throwable
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _uiState.update {
                            it.copy(
                                loading = true
                            )
                        }
                    }

                    is ResultState.Success -> {
                        _uiState.update {
                            it.copy(
                                loading = false
                            )
                        }
                        onSuccess()
                    }
                }
            }
        }
    }

    fun updateLikeCount(isLike: Boolean, userModel: UserModel, postModel: PostModel) {
        viewModelScope.launch {}
    }


    fun hidePost(onSuccess: () -> Unit) {
        viewModelScope.launch {
        }
    }

    fun banUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            banUserUseCase(uiState.value.writerUser.uid).collect { result ->
                when (result) {
                    is ResultState.Failure -> {}
                    ResultState.Loading -> {}
                    is ResultState.Success -> {
                        onSuccess()
                    }
                }
            }
        }
    }
}