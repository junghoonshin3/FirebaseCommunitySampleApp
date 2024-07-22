package kr.sjh.presentation.ui.board.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.usecase.board.AddPostUseCase
import javax.inject.Inject

data class WriteUiState(
    val loading: Boolean = false,
    val post: PostModel = PostModel(),
    val completedPostKey: String = "",
    val error: Throwable? = null
)

@HiltViewModel
class BoardWriteViewModel @Inject constructor(
    private val addPostUseCase: AddPostUseCase,
) : ViewModel() {
    private val _writeUiState: MutableStateFlow<WriteUiState> =
        MutableStateFlow(WriteUiState())
    val writeUiState = _writeUiState.asStateFlow()

    fun updateTitle(title: String) {
        _writeUiState.update {
            it.copy(
                post = it.post.copy(title = title)
            )
        }
    }

    fun updateContent(content: String) {
        _writeUiState.update {
            it.copy(
                post = it.post.copy(content = content)
            )
        }
    }

    fun setSelectedImages(images: List<String>) {
        _writeUiState.update {
            val newImages = it.post.images.toMutableList()
            newImages.addAll(images)
            it.copy(
                post = it.post.copy(images = newImages)
            )
        }
    }

    fun removeSelectedImages(image: String) {
        _writeUiState.update {
            val newImages = it.post.images.toMutableList()
            if (newImages.remove(image)) {
                it.copy(
                    post = it.post.copy(
                        images = newImages
                    )
                )
            } else {
                it
            }
        }
    }

    fun addPost(post: PostModel) =
        viewModelScope.launch {
            addPostUseCase(
                post
            ).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _writeUiState.update {
                            it.copy(
                                loading = false,
                                error = result.throwable
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _writeUiState.update {
                            it.copy(
                                loading = true
                            )
                        }
                    }

                    is ResultState.Success -> {
                        _writeUiState.update {
                            it.copy(
                                loading = false,
                                completedPostKey = result.data
                            )
                        }
                    }
                }
            }
        }
}