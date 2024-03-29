package kr.sjh.presentation.ui.board.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.sjh.domain.usecase.board.UpdatePostUseCase
import kr.sjh.domain.usecase.login.firebase.UpdateUserUseCase
import kr.sjh.domain.usecase.login.model.Post
import kr.sjh.domain.usecase.login.model.UserInfo
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BoardDetailViewModel @Inject constructor(
    private val updatePostUseCase: UpdatePostUseCase,
) : ViewModel() {

    fun updatePostLikeCount(map: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            updatePostUseCase(
                map
            ).onSuccess {

            }.onFailure {

            }
        }
    }

}