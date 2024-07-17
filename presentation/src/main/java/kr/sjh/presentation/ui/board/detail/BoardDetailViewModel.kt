package kr.sjh.presentation.ui.board.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.usecase.board.GetPostUseCase
import kr.sjh.domain.usecase.user.HideUserUseCase
import kr.sjh.domain.usecase.board.RemovePostUseCase
import kr.sjh.domain.usecase.user.BanUserUseCase
import javax.inject.Inject

sealed interface DetailUiState {
    data class Success(val data: Pair<PostModel, UserModel>) : DetailUiState
    data class Error(val throwable: Throwable) : DetailUiState
    data object Loading : DetailUiState
    data object Init : DetailUiState
}

sealed interface DetailBottomSheetUiState {
    data object Success : DetailBottomSheetUiState
    data class Error(val throwable: Throwable) : DetailBottomSheetUiState
    data object Loading : DetailBottomSheetUiState
    data object Init : DetailBottomSheetUiState
}


@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    private val removePostUseCase: RemovePostUseCase,
    private val hideUserUseCase: HideUserUseCase,
    private val banUserUseCase: BanUserUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val postKey = savedStateHandle.get<String>("postKey").orEmpty()

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Init)
    val uiState = _uiState.asStateFlow()

    private val _bottomSheetUiState =
        MutableStateFlow<DetailBottomSheetUiState>(DetailBottomSheetUiState.Init)
    val bottomSheetUiState = _bottomSheetUiState.asStateFlow()

    init {
        getPost()
    }


    private fun getPost() {
        viewModelScope.launch {
            getPostUseCase(postKey).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _uiState.value = DetailUiState.Error(result.throwable)
                    }

                    ResultState.Loading -> _uiState.value = DetailUiState.Loading
                    is ResultState.Success -> {
                        _uiState.value = DetailUiState.Success(result.data)
                    }
                }
            }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            removePostUseCase(postKey).collect {
                when (it) {
                    is ResultState.Failure -> {
                        _bottomSheetUiState.value = DetailBottomSheetUiState.Error(it.throwable)
                    }

                    ResultState.Loading -> {
                        _bottomSheetUiState.value = DetailBottomSheetUiState.Loading
                    }

                    is ResultState.Success -> {
                        _bottomSheetUiState.value = DetailBottomSheetUiState.Success
                    }
                }
            }
        }
    }

    fun updateLikeCount(isLike: Boolean, userModel: UserModel, postModel: PostModel) {
        viewModelScope.launch {}
    }


    fun hideUser(writerUid: String) {
        viewModelScope.launch {
            hideUserUseCase(writerUid).collect {
                when (it) {
                    is ResultState.Failure -> {}
                    ResultState.Loading -> {}
                    is ResultState.Success -> {}
                }
            }
        }
    }

    fun banUser(writerUid: String) {
        viewModelScope.launch {
            banUserUseCase(writerUid).collect {
                when (it) {
                    is ResultState.Failure -> {}
                    ResultState.Loading -> {}
                    is ResultState.Success -> {}
                }
            }
        }
    }
}