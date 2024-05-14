package kr.sjh.presentation.ui.board

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
import kr.sjh.domain.usecase.board.ReadPostsUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.domain.usecase.login.model.Post
import javax.inject.Inject

sealed interface BoardUiState {
    data class Success(val list: List<Post>) : BoardUiState
    data object Loading : BoardUiState

    data object Empty : BoardUiState

    data class Error(val throwable: Throwable) : BoardUiState
}

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val readPostsUseCase: ReadPostsUseCase,
    private val updatePostUseCase: UpdatePostUseCase
) : ViewModel() {

    val posts: StateFlow<BoardUiState> =
        flow {
            emit(BoardUiState.Loading)
            readPostsUseCase()
                .collect { posts ->
                    if (posts.isEmpty()) {
                        emit(BoardUiState.Empty)
                    } else {
                        emit(BoardUiState.Success(posts))
                    }
                }
        }.catch {
            emit(BoardUiState.Error(it))
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BoardUiState.Loading
        )


//    fun getPosts() = flow<BoardUiState> {
//        Log.i("sjh", "??")
//        _posts.emit(BoardUiState.Loading)
//        readPostsUseCase()
//            .collect {
//                if (it.isNotEmpty()) {
//                    _posts.emit(BoardUiState.Success(it))
//                } else {
//                    _posts.emit(BoardUiState.Empty)
//                }
//            }
//    }.catch {
//        _posts.emit(BoardUiState.Error(it))
//    }

    fun postUpdate(post: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            updatePostUseCase(post)
                .onSuccess {
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }
}