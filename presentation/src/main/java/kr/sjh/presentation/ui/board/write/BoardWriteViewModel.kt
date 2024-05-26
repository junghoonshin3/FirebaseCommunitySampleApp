package kr.sjh.presentation.ui.board.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.model.Post
import kr.sjh.domain.model.UserInfo
import kr.sjh.domain.usecase.login.firebase.ReadUserUseCase
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BoardWriteViewModel @Inject constructor(
    private val createPostsUseCase: CreatePostUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val readUserUseCase: ReadUserUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var title by mutableStateOf("")

    var content by mutableStateOf("")

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: String) {
        this.content = content
    }

    fun createPost(userId: String) {
        viewModelScope.launch {
            readUserUseCase(userId).mapCatching { userInfo ->
                createPostsUseCase(
                    Post(
                        writerId = userInfo.id!!,
                        nickName = userInfo.nickName!!,
                        title = title,
                        content = content,
                        createdAt = Date().time
                    )
                ).onSuccess {
                    updateUserUseCase(
                        userInfo.copy(
                            postCount = userInfo.postCount.plus(1)
                        )
                    ).getOrThrow()
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }
}