package kr.sjh.presentation.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.usecase.board.GetPostsUseCase
import kr.sjh.domain.usecase.board.UpdatePostCountUseCase
import javax.inject.Inject

data class BoardUiState(
    val isLoading: Boolean = false,
    val posts: List<PostModel> = emptyList(),
    val error: Throwable? = null
)

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val postsUseCase: GetPostsUseCase,
    private val updatePostCountUseCase: UpdatePostCountUseCase
) : ViewModel() {

    private val _postUiState = MutableStateFlow(BoardUiState())
    val postUiState = _postUiState.asStateFlow()

    init {
        getPosts()
    }

    fun nextPosts() {
        val title = _postUiState.value.posts.last().title
        val lastTime = _postUiState.value.posts.last().timeStamp.time
        Log.d("sjh", "title : ${title}, last : $lastTime")
        getPosts(size = 5, lastTime = lastTime)
    }

    private fun getPosts(size: Long = 10, lastTime: Long? = null) {
        viewModelScope.launch {
            postsUseCase(size = size, lastTime).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _postUiState.update {
                            it.copy(isLoading = false, error = result.throwable)
                        }
                    }

                    ResultState.Loading -> {
                        _postUiState.update {
                            it.copy(isLoading = true, error = null)
                        }
                    }

                    is ResultState.Success -> {
                        _postUiState.update {
                            it.copy(isLoading = false, posts = it.posts + result.data, error = null)
                        }
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