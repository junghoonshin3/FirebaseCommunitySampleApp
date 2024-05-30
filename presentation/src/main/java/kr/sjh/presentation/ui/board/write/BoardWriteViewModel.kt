package kr.sjh.presentation.ui.board.write

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.model.Post
import kr.sjh.domain.usecase.board.CreatePostKeyUseCase
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.board.UploadMultiImageUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import java.util.Date
import javax.inject.Inject

sealed interface WriteUiState {
    data object Init : WriteUiState
    data object Loading : WriteUiState

    data class Success(val postKey: String) : WriteUiState

    data class Error(val throwable: Throwable) : WriteUiState

}

@HiltViewModel
class BoardWriteViewModel @Inject constructor(
    private val createPostKeyUseCase: CreatePostKeyUseCase,
    private val createPostsUseCase: CreatePostUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val readUserUseCase: ReadUserUseCase,
    private val uploadMultiImageUseCase: UploadMultiImageUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var title by mutableStateOf("")

    var content by mutableStateOf("")

    private val _uiState: MutableStateFlow<WriteUiState> =
        MutableStateFlow(WriteUiState.Init)
    val uiState = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: String) {
        this.content = content
    }

    fun createPost(userId: String, uris: List<Uri>) =
        viewModelScope.launch {
            _uiState.emit(WriteUiState.Loading)

            runCatching {
                val userInfo = readUserUseCase(userId).getOrThrow()
                val postKey = createPostKeyUseCase()
                val downLoadUrls = if (uris.isNotEmpty()) {
                    uploadMultiImageUseCase(postKey, uris).getOrThrow()
                        .map { it.toString() }
                } else {
                    emptyList()
                }
                val post = Post(
                    writerId = userInfo.id.toString(),
                    key = postKey,
                    title = title,
                    content = content,
                    nickName = userInfo.nickName,
                    createdAt = Date().time,
                    images = downLoadUrls
                )
                createPostsUseCase(post).getOrThrow()
                updateUserUseCase(userInfo.copy(postCount = userInfo.postCount.plus(1))).getOrThrow()
                postKey
            }.onSuccess { key ->
                _uiState.emit(WriteUiState.Success(key))
            }.onFailure {
                _uiState.emit(WriteUiState.Error(it))
            }
        }
}