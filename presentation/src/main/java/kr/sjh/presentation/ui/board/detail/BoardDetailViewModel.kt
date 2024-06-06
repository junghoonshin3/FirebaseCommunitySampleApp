package kr.sjh.presentation.ui.board.detail

import android.util.Log
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
import kr.sjh.domain.usecase.board.DeletePostUseCase
import kr.sjh.domain.usecase.board.ReadPostUseCase
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.model.Post
import kr.sjh.domain.model.UserInfo
import kr.sjh.domain.usecase.board.RemoveImagesUsaCase
import javax.inject.Inject

sealed interface DetailUiState {
    data class Success(val pair: Pair<Post, UserInfo>) : DetailUiState
    data class Error(val throwable: Throwable) : DetailUiState
    data object Loading : DetailUiState
    data object Init : DetailUiState
}


@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    private val updateUserUseCase: UpdateUserUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val readPostUseCase: ReadPostUseCase,
    private val readUserUseCase: ReadUserUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val removeImagesUsaCase: RemoveImagesUsaCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val postKey = savedStateHandle.get<String>("postKey")

    val detailUiState: StateFlow<DetailUiState> = readPostUseCase(postKey.toString())
        .map { post ->
            Log.d("sjh", "post : ${post.images.size}")
            val userInfo = readUserUseCase(post.writerId).getOrThrow()
            Pair(post, userInfo)
        }
        .map<Pair<Post, UserInfo>, DetailUiState>(DetailUiState::Success)
        .onStart { emit(DetailUiState.Loading) }
        .catch {
            emit(DetailUiState.Error(it))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetailUiState.Loading
        )


    fun deletePost(post: Post) {
        viewModelScope.launch {
            runCatching {
                val userInfo = readUserUseCase(post.writerId).getOrThrow()
                deletePostUseCase(post.key).getOrThrow()

                if (post.images.isNotEmpty()) {
                    removeImagesUsaCase(post.key).getOrThrow()
                }

                // '좋아요 리스트'에 글이 포함된 경우
                // 아닌 경우 기존 리스트 리턴
                val newLikePosts = if (userInfo.likePosts.contains(post.key)) {
                    userInfo.likePosts.toMutableList().apply {
                        remove(post.key)
                    }
                } else {
                    userInfo.likePosts
                }

                //내가 쓴 글 1 뺴기
                updateUserUseCase(
                    userInfo.copy(
                        likePosts = newLikePosts,
                        postCount = userInfo.postCount.minus(1)
                    )
                ).getOrThrow()
            }.onSuccess {
            }
                .onFailure {
                    it.printStackTrace()
                }

        }
    }

    fun updateLikeCount(isLike: Boolean, userInfo: UserInfo, post: Post) {
        viewModelScope.launch {
            updatePostUseCase(
                post.copy(
                    likeCount = if (isLike) post.likeCount.plus(1) else post.likeCount.minus(1)
                )
            ).mapCatching {
                val likes = userInfo.likePosts.toMutableList()
                if (isLike) {
                    likes.add(post.key)
                } else {
                    likes.remove(post.key)
                }
                updateUserUseCase(
                    userInfo.copy(
                        likePosts = likes.toList()
                    )
                ).getOrThrow()
            }.onSuccess {
                Log.d("sjh", "updateLikeCount :$it")
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}