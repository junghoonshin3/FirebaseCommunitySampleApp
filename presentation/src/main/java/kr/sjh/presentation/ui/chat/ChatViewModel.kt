package kr.sjh.presentation.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import kr.sjh.domain.usecase.chat.GetChatRoomsUseCase
import kr.sjh.domain.util.getReceiverUid
import javax.inject.Inject

data class ChatRoomUiState(
    val uid: String = "",
    val rooms: List<ChatRoomModel> = emptyList(),
    val isLoading: Boolean = false,
    val throwable: Throwable? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRoomsUseCase: GetChatRoomsUseCase,
    private val authCurrentUserUseCase: GetAuthCurrentUserUseCase
) : ViewModel() {

    private val _chatRooms = MutableStateFlow(ChatRoomUiState())
    val chatRooms = _chatRooms.asStateFlow()

    init {
        getChatRooms()
    }

    private fun getChatRooms() {
        viewModelScope.launch {
            chatRoomsUseCase().collect { result ->
                when (result) {
                    is ResultState.Failure -> {}
                    ResultState.Loading -> {}
                    is ResultState.Success -> {
                        _chatRooms.update {
                            it.copy(
                                uid = authCurrentUserUseCase()?.uid.toString(),
                                rooms = result.data
                            )
                        }
                    }
                }
            }
        }
    }
}