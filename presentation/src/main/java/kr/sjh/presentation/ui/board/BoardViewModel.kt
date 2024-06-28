package kr.sjh.presentation.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.usecase.board.GetPostsUseCase
import kr.sjh.domain.usecase.board.UpdatePostCountUseCase
import javax.inject.Inject

sealed interface BoardUiState {
    data object Init : BoardUiState
    data class Success(val posts: List<PostModel>) : BoardUiState
    data object Loading : BoardUiState
    data class Error(val throwable: Throwable) : BoardUiState
}

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val postsUseCase: GetPostsUseCase,
    private val updatePostCountUseCase: UpdatePostCountUseCase
) : ViewModel() {

    private val _postUiState = MutableStateFlow<BoardUiState>(BoardUiState.Init)
    val postUiState = _postUiState.asStateFlow()

    fun getPosts() {
        viewModelScope.launch {
            postsUseCase().collect {
                when (it) {
                    is ResultState.Failure -> {
                        _postUiState.value = BoardUiState.Error(it.throwable)
                    }

                    ResultState.Loading -> {
                        _postUiState.value = BoardUiState.Loading
                    }

                    is ResultState.Success -> {
                        _postUiState.value = BoardUiState.Success(it.data)
                    }
                }
            }
        }
    }

    fun updatePostCount(postKey: String) {
        viewModelScope.launch {
            updatePostCountUseCase(postKey).collect {
                when (it) {
                    is ResultState.Failure -> {
                        it.throwable.printStackTrace()
                    }

                    ResultState.Loading -> {

                    }

                    is ResultState.Success -> {
                        Log.d("sjh", "Success")
                    }
                }
            }
        }

    }


}