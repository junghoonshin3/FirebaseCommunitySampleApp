package kr.sjh.presentation.ui.board

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.PostModel
import kr.sjh.domain.usecase.board.GetPostsUseCase
import kr.sjh.domain.usecase.board.UpdatePostCountUseCase
import kr.sjh.presentation.constants.LOAD_ITEM_COUNT
import kr.sjh.presentation.constants.VISIBLE_ITEM_COUNT
import kr.sjh.presentation.ui.common.RefreshingType
import javax.inject.Inject

data class BoardUiState(
    val isLoading: Boolean = false,
    val isLoadMore: Boolean = false,
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

    private val _isRefreshing = MutableSharedFlow<RefreshingType>()
    val isRefreshing = _isRefreshing.asSharedFlow()

    init {
        initPosts()
    }

    fun refreshPosts() {
        viewModelScope.launch {
            postsUseCase(VISIBLE_ITEM_COUNT, null).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _isRefreshing.emit(RefreshingType.END)
                        _postUiState.update {
                            it.copy(
                                error = result.throwable
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _isRefreshing.emit(RefreshingType.START)
                        _postUiState.update {
                            it.copy(error = null)
                        }
                    }

                    is ResultState.Success -> {
                        _isRefreshing.emit(RefreshingType.END)
                        _postUiState.update {
                            it.copy(
                                posts = result.data, error = null
                            )
                        }
                    }
                }
            }
        }
    }

    fun nextPosts() {
        val lastTime = _postUiState.value.posts.last().timeStamp.time
        viewModelScope.launch {
            postsUseCase(size = VISIBLE_ITEM_COUNT, lastTime).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _postUiState.update {
                            it.copy(
                                isLoadMore = false, error = result.throwable
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _postUiState.update {
                            it.copy(isLoadMore = true, error = null)
                        }
                    }

                    is ResultState.Success -> {
                        _postUiState.update {
                            it.copy(
                                isLoadMore = false, posts = it.posts + result.data, error = null
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initPosts(
    ) {
        viewModelScope.launch {
            postsUseCase(size = VISIBLE_ITEM_COUNT, lastTime = null).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _postUiState.update {
                            it.copy(
                                isLoading = false, error = result.throwable
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _postUiState.update {
                            it.copy(isLoading = true, error = null)
                        }
                    }

                    is ResultState.Success -> {
                        _postUiState.update {
                            it.copy(
                                isLoading = false, posts = result.data, error = null
                            )
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