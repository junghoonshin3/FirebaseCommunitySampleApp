package kr.sjh.presentation.ui.board.edit

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.ReadPostUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.domain.model.Post
import javax.inject.Inject

sealed interface BoardEditUiState {
    data object Success : BoardEditUiState

    data class Error(val throwable: Throwable) : BoardEditUiState

    data object Loading : BoardEditUiState

    data object Init : BoardEditUiState
}

@HiltViewModel
class BoardEditViewModel @Inject constructor(
    private val updatePostUseCase: UpdatePostUseCase,
    private val readPostUseCase: ReadPostUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postKey = savedStateHandle.get<String>("postKey")

    var title by mutableStateOf("")

    var content by mutableStateOf("")

    init {
        Log.d("sjh", "$postKey")
    }

    val post: StateFlow<Post> = readPostUseCase(postKey.toString()).map {
        title = it.title ?: ""
        content = it.content ?: ""
        it
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Post()
    )
    private val _editUiState: MutableStateFlow<BoardEditUiState> =
        MutableStateFlow(BoardEditUiState.Init)
    val editUiState = _editUiState.asStateFlow()

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: String) {
        this.content = content
    }

    fun updatePost(post: Post) {
        viewModelScope.launch {
            _editUiState.emit(BoardEditUiState.Loading)
            updatePostUseCase(
                post.copy(
                    title = title,
                    content = content,
                )
            ).onSuccess {
                _editUiState.emit(BoardEditUiState.Success)
            }.onFailure {
                _editUiState.emit(BoardEditUiState.Error(it))
            }
        }
    }
}