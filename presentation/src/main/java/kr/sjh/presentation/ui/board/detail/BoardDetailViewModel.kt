package kr.sjh.presentation.ui.board.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.ReadPostUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.model.Post
import kr.sjh.domain.usecase.login.model.UserInfo
import javax.inject.Inject

sealed interface WriteUiState {
    data class Success(val userInfo: UserInfo, val post: Post) : WriteUiState
    data class Error(val throwable: Throwable) : WriteUiState
    data object Loading : WriteUiState
}

@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    private val updatePostUseCase: UpdatePostUseCase,
    private val readPostUseCase: ReadPostUseCase,
    private val readUserUseCase: ReadUserUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val post = savedStateHandle.get<Post>("post")

    val writeUiState: StateFlow<WriteUiState> = flow {
        emit(WriteUiState.Loading)
        post?.let {
            val userInfo = readUserUseCase(post.writerId).getOrThrow()
            emit(WriteUiState.Success(userInfo, post))
        }
    }.catch {
        emit(WriteUiState.Error(it))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = WriteUiState.Loading
    )

    fun updatePostLikeCount(map: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun deletePost() {
        Log.d("sjh", "delete Post")
    }

}