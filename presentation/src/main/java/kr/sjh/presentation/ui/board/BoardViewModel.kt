package kr.sjh.presentation.ui.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.board.ReadPostsUseCase
import kr.sjh.domain.usecase.login.model.Post
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val readPostsUseCase: ReadPostsUseCase,
    private val createPostsUseCase: CreatePostUseCase
) : ViewModel() {

    init {
        getPosts()
    }

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts = _posts.asStateFlow()
    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            readPostsUseCase()
                .collect {
                    _posts.value = it
                }
        }
    }

    fun createPost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            createPostsUseCase(post)
                .onSuccess {

                }
                .onFailure {

                }
        }
    }
}