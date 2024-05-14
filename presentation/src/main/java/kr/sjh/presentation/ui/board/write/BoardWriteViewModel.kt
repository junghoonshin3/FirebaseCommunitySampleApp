package kr.sjh.presentation.ui.board.write

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.model.Post
import kr.sjh.domain.usecase.login.model.UserInfo
import java.util.Date
import javax.inject.Inject

sealed interface BoardWriteUiState {
    data object Success : BoardWriteUiState

    data class Error(val throwable: Throwable) : BoardWriteUiState

    data object Loading : BoardWriteUiState
}

@HiltViewModel
class BoardWriteViewModel @Inject constructor(
    private val createPostsUseCase: CreatePostUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val readUserUseCase: ReadUserUseCase
) : ViewModel() {

    var title = savedStateHandle.getStateFlow("title", "")

    var content = savedStateHandle.getStateFlow("content", "")

    val userInfo = savedStateHandle.get<UserInfo>("userInfo")

    private val _writeUiState: MutableSharedFlow<BoardWriteUiState> = MutableSharedFlow()

    val writeUiState: SharedFlow<BoardWriteUiState> = _writeUiState.asSharedFlow()

    fun updateTitle(title: String) {
        savedStateHandle["title"] = title
    }

    fun updateContent(content: String) {
        savedStateHandle["content"] = content
    }

    fun createPost() =
        viewModelScope.launch {
            _writeUiState.emit(BoardWriteUiState.Loading)
            userInfo?.let {
                createPostsUseCase(
                    Post(
                        writerId = it.id!!,
                        title = title.value,
                        content = content.value,
                        nickName = it.nickName,
                        createdAt = Date().time
                    )
                ).onSuccess {
                    _writeUiState.emit(BoardWriteUiState.Success)
                }
                    .onFailure {
                        _writeUiState.emit(BoardWriteUiState.Error(it))
                    }
            }
        }
//            _writeUiState.emit(BoardWriteUiState.Loading)
////                Log.d("sjh", "userInfo : ${userInfo.id}")
//            createPostsUseCase(
//                Post(
//                    writerId = userInfo.id!!,
//                    title = title.value,
//                    content = content.value,
//                    nickName = userInfo.nickName,
//                    createdAt = Date().time
//                )
//            ).getOrThrow()
//        }.catch {
//            it.printStackTrace()
//            _writeUiState.emit(BoardWriteUiState.Error(it))
//        }.collect {
//            _writeUiState.emit(BoardWriteUiState.Success)
//        }
}