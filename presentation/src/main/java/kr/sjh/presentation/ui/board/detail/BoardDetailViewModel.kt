package kr.sjh.presentation.ui.board.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.DeletePostUseCase
import kr.sjh.domain.usecase.board.ReadPostUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.model.Post
import kr.sjh.model.UserInfo
import javax.inject.Inject

sealed interface DetailUiState {
    data class Success(val userInfo: UserInfo, val post: Post) : DetailUiState
    data class Error(val throwable: Throwable) : DetailUiState
    data object Loading : DetailUiState
}

@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    private val updatePostUseCase: UpdatePostUseCase,
    private val readPostUseCase: ReadPostUseCase,
    private val readUserUseCase: ReadUserUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val post = savedStateHandle.get<Post>("post")

    val detailUiState: StateFlow<DetailUiState> = flow {
        emit(DetailUiState.Loading)
        post?.let {
            val userInfo = readUserUseCase(post.writerId).getOrThrow()
            emit(DetailUiState.Success(userInfo, post))
        }
    }.catch {
        emit(DetailUiState.Error(it))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailUiState.Loading
    )

    fun updatePostLikeCount(map: Map<String, Any>) {
        viewModelScope.launch {

        }
    }

    fun deletePost(post: Post, onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        Log.d("sjh", "delete Post")
        viewModelScope.launch {
            deletePostUseCase.invoke(post)
                .onSuccess {
                    onSuccess()
                }
                .onFailure {
                    it.printStackTrace()
                    onFailure(it)
                }
        }
    }

}