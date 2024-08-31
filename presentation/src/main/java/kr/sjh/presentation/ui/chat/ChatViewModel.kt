package kr.sjh.presentation.ui.chat

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import kr.sjh.domain.usecase.chat.GetChatRoomsUseCase
import kr.sjh.domain.usecase.chat.RemoveChatRoomUseCase
import kr.sjh.domain.util.getReceiverUid
import kr.sjh.presentation.ui.common.RefreshingType
import javax.inject.Inject

@Stable
data class ChatRoomUiState(
    val uid: String = "",
    val rooms: List<ChatRoomModel> = emptyList(),
    val isLoading: Boolean = false,
    val throwable: Throwable? = null,
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRoomsUseCase: GetChatRoomsUseCase,
    private val removeChatRoomUseCase: RemoveChatRoomUseCase,
    authCurrentUserUseCase: GetAuthCurrentUserUseCase
) : ViewModel() {

    private val _chatRooms = MutableStateFlow(ChatRoomUiState())
    val chatRooms = _chatRooms.asStateFlow()

    private val _isRefreshing = MutableSharedFlow<RefreshingType>()
    val isRefreshing = _isRefreshing.asSharedFlow()

    private val uid = authCurrentUserUseCase()?.uid.toString()

    init {
        getChatRooms()
    }

    private fun getChatRooms() {
        viewModelScope.launch {
            chatRoomsUseCase()
                .collect { result ->
                    when (result) {
                        is ResultState.Failure -> {
                            Log.d("sjh", "ResultState.Failure")
                            _chatRooms.update {
                                it.copy(isLoading = false, throwable = result.throwable)
                            }
                        }

                        ResultState.Loading -> {
                            Log.d("sjh", "ResultState.Loading")
                            _chatRooms.update {
                                it.copy(isLoading = true)
                            }
                        }

                        is ResultState.Success -> {
                            Log.d("sjh", "ResultState.Success")
                            _chatRooms.update {
                                it.copy(
                                    isLoading = false,
                                    uid = uid,
                                    rooms = result.data,
                                    throwable = null
                                )
                            }
                        }
                    }
                }
        }
    }

    fun refreshChatRooms() {
        viewModelScope.launch {
            chatRoomsUseCase()
                .onStart { _isRefreshing.emit(RefreshingType.START) }
                .collect { result ->
                    when (result) {
                        is ResultState.Failure -> {
                            _isRefreshing.emit(RefreshingType.END)
                        }

                        ResultState.Loading -> {
                            _isRefreshing.emit(RefreshingType.START)
                        }

                        is ResultState.Success -> {
                            _isRefreshing.emit(RefreshingType.END)
                            _chatRooms.update {
                                it.copy(
                                    isLoading = false,
                                    uid = uid,
                                    rooms = result.data,
                                    throwable = null
                                )
                            }
                        }
                    }
                }
        }
    }

    fun removeChatRoom(chatRoom: ChatRoomModel) {
        viewModelScope.launch {
            removeChatRoomUseCase(chatRoom).collect {
                when (it) {
                    is ResultState.Failure -> {}
                    ResultState.Loading -> {}
                    is ResultState.Success -> {}
                }
            }
        }

    }
}