package kr.sjh.presentation.ui.chat

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kr.sjh.domain.ResultState
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.usecase.auth.firebase.GetAuthCurrentUserUseCase
import kr.sjh.domain.usecase.chat.GetInitialMessagesUseCase
import kr.sjh.domain.usecase.chat.GetNextMessagesUseCase
import kr.sjh.domain.usecase.chat.SendMessageUseCase
import kr.sjh.domain.usecase.chat.UpdateLastVisitedTimeStampUseCase
import kr.sjh.domain.util.getReceiverUid
import kr.sjh.presentation.constants.LOAD_ITEM_COUNT
import kr.sjh.presentation.constants.VISIBLE_ITEM_COUNT
import javax.inject.Inject

@Stable
data class MessageUiState(
    val messages: List<ChatMessageModel> = emptyList(),
    val nickName: String = "",
    val profileImageUrl: String = "",
    val isLoading: Boolean = false,
    val throwable: Throwable? = null
)

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getInitialMessagesUseCase: GetInitialMessagesUseCase,
    private val getNextMessagesUseCase: GetNextMessagesUseCase,
    getAuthCurrentUserUseCase: GetAuthCurrentUserUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val updateLastVisitedTimeStampUseCase: UpdateLastVisitedTimeStampUseCase
) : ViewModel() {

    private val roomId = savedStateHandle.get<String>("roomId").toString()

    private val nickName = savedStateHandle.get<String>("nickName").toString()

    private val profileImageUrl = savedStateHandle.get<String>("profileImageUrl").toString()

    private val uid = getAuthCurrentUserUseCase()?.uid.toString()

    var message by mutableStateOf(savedStateHandle["message"] ?: "")

    private val _messageUiState = MutableStateFlow(
        MessageUiState(
            nickName = nickName, profileImageUrl = profileImageUrl
        )
    )
    val messageUiState = _messageUiState.asStateFlow()

    init {
        getInitialMessages()

    }

    private fun getInitialMessages(
        size: Long = VISIBLE_ITEM_COUNT
    ) {
        viewModelScope.launch {
            getInitialMessagesUseCase(
                roomId = roomId, size = size
            ).collect { result ->
                when (result) {
                    is ResultState.Failure -> {
                        _messageUiState.update {
                            it.copy(
                                isLoading = false, throwable = result.throwable
                            )
                        }
                    }

                    ResultState.Loading -> {
                        _messageUiState.update {
                            it.copy(
                                isLoading = true
                            )
                        }
                    }

                    is ResultState.Success -> {
                        _messageUiState.update {
                            it.copy(
                                isLoading = false, messages = result.data
                            )
                        }
                    }
                }
            }
        }
    }

    fun getNextMessages(limit: Long = LOAD_ITEM_COUNT) {
        viewModelScope.launch {
            messageUiState.value.messages.last().timeStamp?.let { timeStamp ->
                getNextMessagesUseCase(
                    roomId = roomId, limit = limit, fromTime = timeStamp.time
                ).collect { result ->
                    when (result) {
                        is ResultState.Failure -> {
                            Log.d("sjh", "Failure")
                            _messageUiState.update {
                                it.copy(
                                    isLoading = false, throwable = result.throwable
                                )
                            }
                        }

                        ResultState.Loading -> {
                            _messageUiState.update {
                                it.copy(
                                    isLoading = true, throwable = null
                                )
                            }
                        }

                        is ResultState.Success -> {
                            Log.d("getNextMessages", "Success > ${result.data}")
                            _messageUiState.update {
                                it.copy(
                                    isLoading = false,
                                    messages = it.messages + result.data,
                                    throwable = null
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    fun changeTextMessage(message: String) {
        savedStateHandle["message"] = message
        this.message = message
    }

    fun sendMessage(onSuccess: () -> Unit) {
        if (message.isNotEmpty()) {
            viewModelScope.launch {
                val newMessage = ChatMessageModel(
                    senderUid = uid, receiverUid = getReceiverUid(roomId, uid), text = message
                )
                sendMessageUseCase(newMessage).collect { result ->
                    when (result) {
                        is ResultState.Success -> {
                            onSuccess()
                        }

                        is ResultState.Failure -> {
                            result.throwable.printStackTrace()
                        }
                        ResultState.Loading -> {}
                    }
                }
            }
            changeTextMessage("")
        }
    }

    fun updateLastVisitedTimeStamp() {
        viewModelScope.launch {
            updateLastVisitedTimeStampUseCase(roomId)
        }
    }

}