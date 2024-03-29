package kr.sjh.presentation.ui.board.write

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.CreatePostUseCase
import kr.sjh.domain.usecase.board.ReadPostsUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.usecase.login.model.Post
import kr.sjh.domain.usecase.login.model.UserInfo
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BoardWriteViewModel @Inject constructor(
    private val readPostsUseCase: ReadPostsUseCase,
    private val createPostsUseCase: CreatePostUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    var title by mutableStateOf("")
    var content by mutableStateOf("")

    fun updateTitle(title: String) {
        this.title = title
    }

    fun updateContent(content: String) {
        this.content = content
    }

    fun createPost(userInfo: UserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            createPostsUseCase(
                Post(
                    id = userInfo.id,
                    title = title,
                    content = content,
                    nickName = userInfo.nickName,
                    createdAt = Date().time
                )
            )
                .onSuccess {
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }
}