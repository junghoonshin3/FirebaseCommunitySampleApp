package kr.sjh.presentation.ui.board.write

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.model.Post
import kr.sjh.model.UserInfo
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
    private val updatePostUseCase: UpdatePostUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val post = savedStateHandle.get<Post?>("post")

    var title = savedStateHandle.getStateFlow("title", post?.title ?: "")

    var content = savedStateHandle.getStateFlow("content", post?.content ?: "")

    private val _writeUiState: MutableStateFlow<BoardWriteUiState> =
        MutableStateFlow(BoardWriteUiState.Loading)

    val writeUiState: SharedFlow<BoardWriteUiState> = _writeUiState.asStateFlow()

    init {
        Log.d("sjh", "post :${savedStateHandle.get<Post?>("post")?.writerId}")
    }

    fun updateTitle(title: String) {
        savedStateHandle["title"] = title
    }

    fun updateContent(content: String) {
        savedStateHandle["content"] = content
    }

    fun createPost(userInfo: UserInfo) {
        viewModelScope.launch {
            createPostsUseCase(
                Post(
                    writerId = userInfo.id!!,
                    title = title.value,
                    content = content.value,
                    nickName = userInfo.nickName,
                    createdAt = Date().time
                )
            ).onSuccess {

            }.onFailure {

            }
        }
    }
//        viewModelScope.launch {
//            userInfo?.let {
//                createPostsUseCase(
//                    Post(
//                        writerId = it.id!!,
//                        title = title.value,
//                        content = content.value,
//                        nickName = it.nickName,
//                        createdAt = Date().time
//                    )
//                ).onSuccess {
//                    _writeUiState.emit(BoardWriteUiState.Success)
//                }
//                    .onFailure {
//                        _writeUiState.emit(BoardWriteUiState.Error(it))
//                    }
//            }
//        }

//    fun updatePost() =
//        viewModelScope.launch {
//            _writeUiState.emit(BoardWriteUiState.Loading)
//            userInfo?.let {
//                Log.d("sjh", "title : ${title.value}")
//                Log.d("sjh", "content : ${content.value}")
//
//                updatePostUseCase(
//                    post?.copy(title = title.value, content = content.value) ?: Post()
//                ).onSuccess {
//                    _writeUiState.emit(BoardWriteUiState.Success)
//                }
//                    .onFailure {
//                        _writeUiState.emit(BoardWriteUiState.Error(it))
//                    }
//            }
//
//        }
}