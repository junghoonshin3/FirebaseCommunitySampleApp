package kr.sjh.presentation.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import kr.sjh.domain.usecase.chat.GetChatRoomsUseCase
import javax.inject.Inject

data class ChatRoomUiState(
    val rooms: List<ChatRoomModel> = emptyList(),
    val isLoading: Boolean = false,
    val throwable: Throwable? = null
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getAuthCurrentUserUseCase: GetAuthCurrentUserUseCase,
    private val chatRoomsUseCase: GetChatRoomsUseCase
) : ViewModel() {

    private val _chatRooms = MutableStateFlow(ChatRoomUiState())
    val chatRooms = _chatRooms.asStateFlow()

    init {
        getChatRooms()
    }

    private fun getChatRooms() {
        viewModelScope.launch {
            getAuthCurrentUserUseCase()?.uid?.let { uid ->
                chatRoomsUseCase(uid).collect { result ->
                    when (result) {
                        is ResultState.Failure -> {}
                        ResultState.Loading -> {}
                        is ResultState.Success -> {
                            _chatRooms.update {
                                it.copy(
                                    rooms = result.data
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}