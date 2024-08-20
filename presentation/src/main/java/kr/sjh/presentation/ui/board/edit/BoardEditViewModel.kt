package kr.sjh.presentation.ui.board.edit

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.usecase.board.GetPostUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import javax.inject.Inject

data class EditUiState(
    val loading: Boolean = false,
    val post: PostModel = PostModel(),
    val isComplete: Boolean = false,
    val error: Throwable? = null
)


@HiltViewModel
class BoardEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val updatePostUseCase: UpdatePostUseCase,
    private val getPostUseCase: GetPostUseCase
) : ViewModel() {

    private val postKey: String? = savedStateHandle.get<String>("postKey")

    private val _editUiState = MutableStateFlow(EditUiState())
    val editUiState = _editUiState.asStateFlow()

    init {
        Log.d("sjh", "${postKey}")
        getPost()
    }

    private fun getPost() {
        viewModelScope.launch {
            postKey?.let {
                getPostUseCase(it).collect { result ->
                    when (result) {
                        is ResultState.Failure -> {
                            result.throwable.printStackTrace()
                            _editUiState.update {
                                it.copy(
                                    loading = false,
                                    error = result.throwable
                                )
                            }
                        }

                        ResultState.Loading -> {
                            _editUiState.update {
                                it.copy(
                                    loading = true,
                                )
                            }
                        }

                        is ResultState.Success -> {
                            Log.d("sjh", "${result.data.first}")
                            _editUiState.update {
                                it.copy(
                                    loading = false,
                                    post = result.data.first,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _editUiState.update {
            it.copy(
                post = it.post.copy(title = title)
            )
        }
    }

    fun updateContent(content: String) {
        _editUiState.update {
            it.copy(
                post = it.post.copy(content = content)
            )
        }
    }

    fun setSelectedImages(images: List<String>) {
        _editUiState.update {
            val newImages = it.post.images.toMutableList()
            newImages.addAll(images)
            it.copy(
                post = it.post.copy(images = newImages)
            )
        }
    }

    fun removeSelectedImage(image: String) {
        _editUiState.update {
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

    fun updatePost(post: PostModel) {
        viewModelScope.launch {
            updatePostUseCase(post)
                .collect { result ->
                    when (result) {
                        is ResultState.Failure -> {
                            _editUiState.update {
                                it.copy(
                                    loading = false,
                                    error = result.throwable
                                )
                            }
                        }

                        ResultState.Loading -> {
                            _editUiState.update {
                                it.copy(
                                    loading = true
                                )
                            }
                        }

                        is ResultState.Success -> {
                            postKey?.let { key ->
                                _editUiState.update {
                                    it.copy(
                                        loading = false,
                                        isComplete = true
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}