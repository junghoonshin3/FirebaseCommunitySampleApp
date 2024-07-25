package kr.sjh.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kr.sjh.domain.model.ChatMessageModel
import kr.sjh.domain.model.UserModel
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.common.ContentTextField
import kr.sjh.presentation.ui.main.MainViewModel
import kr.sjh.presentation.ui.theme.PurpleGrey80
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.getActivity
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ChatDetailRoute(
    viewModel: ChatDetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(getActivity()),
    onBack: () -> Unit,
) {
    val messageUiState by viewModel.messageUiState.collectAsStateWithLifecycle()

    val currentUser by mainViewModel.currentUser.collectAsStateWithLifecycle()

    ChatDetailScreen(
        messageUiState = messageUiState,
        currentUser = currentUser,
        onBack = onBack,
        text = viewModel.message,
        onTextChanged = viewModel::changeTextMessage,
        sendMessage = viewModel::sendMessage,
        nextMessages = viewModel::getNextMessages
    )
}

@Composable
fun ChatDetailScreen(
    messageUiState: MessageUiState,
    currentUser: UserModel,
    onBack: () -> Unit,
    text: String,
    onTextChanged: (String) -> Unit,
    sendMessage: (() -> Unit) -> Unit,
    nextMessages: (limit: Long) -> Unit
) {

    val lazyListState = rememberLazyListState()

    val newText by rememberUpdatedState(newValue = text)

    val coroutineScope = rememberCoroutineScope()

    val isLoadMore = remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > (totalItemsNumber - 4)
        }
    }

    LaunchedEffect(isLoadMore) {
        snapshotFlow {
            isLoadMore.value
        }.distinctUntilChanged().filter { it && lazyListState.layoutInfo.totalItemsCount > 0 }
            .collectLatest {
                nextMessages(10)
            }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .height(60.dp),
            title = messageUiState.nickName,
            backIcon = Icons.Default.ArrowBack,
            onBack = onBack,
            profileImageUrl = messageUiState.profileImageUrl
        )
        Conversation(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            lazyListState = lazyListState,
            messages = messageUiState.messages,
            currentUid = currentUser.uid,
        )
        InputMessage(modifier = Modifier
            .imePadding()
            .fillMaxWidth()
            .heightIn(max = 100.dp, min = 80.dp)
            .padding(5.dp),
            text = newText,
            onTextChanged = onTextChanged,
            sendMessage = {
                sendMessage {
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            })
    }
}

@Composable
fun Conversation(
    modifier: Modifier,
    lazyListState: LazyListState,
    messages: List<ChatMessageModel>,
    currentUid: String,
) {

    val conversations by rememberUpdatedState(newValue = messages)

    LazyColumn(
        state = lazyListState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(10.dp),
        reverseLayout = true
    ) {
        itemsIndexed(conversations, key = { _, item -> item.messageId }) { index, item ->
            val isMe = item.senderUid == currentUid
            ConversationItem(
                modifier = Modifier.fillMaxWidth(), isMe = isMe, chatMessage = item
            )
        }
    }


}


@Composable
fun ConversationItem(
    modifier: Modifier,
    isMe: Boolean,
    chatMessage: ChatMessageModel,
) {
    val maxWidthDp = LocalConfiguration.current.screenWidthDp.dp * 2 / 3
    val dateFormat = SimpleDateFormat("HH:mm")

    Row(
        modifier = modifier,
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom // 텍스트 정렬을 위해 Alignment.Bottom으로 변경
    ) {
        // when 문을 사용하여 isMe에 따라 UI를 다르게 표시
        MessageBubbleWithTime(
            isMe = isMe,
            message = chatMessage.message,
            timestamp = chatMessage.timeStamp,
            dateFormat = dateFormat,
            maxWidthDp = maxWidthDp
        )
    }
}

@Composable
fun MessageBubbleWithTime(
    isMe: Boolean, message: String, timestamp: Date?, dateFormat: SimpleDateFormat, maxWidthDp: Dp
) {
    Column { // 컬럼을 사용하여 메시지 거품과 시간을 배치
        Box(
            modifier = Modifier
                .widthIn(max = maxWidthDp)
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (isMe) 48f else 0f,
                        bottomEnd = if (isMe) 0f else 48f
                    )
                )
                .background(if (isMe) carrot else PurpleGrey80)
                .padding(10.dp)
        ) {
            Text(text = message)
        }
        timestamp?.let { // 시간 표시
            Text(
                text = dateFormat.format(it),
                color = Color.White,
                modifier = Modifier
                    .padding(4.dp)
                    .align(if (isMe) Alignment.End else Alignment.Start) // 시간 텍스트에 패딩 추가
            )
        }
    }
}

@Composable
fun Profile(modifier: Modifier = Modifier, imageUrl: String) {
    GlideImage(modifier = modifier, requestOptions = {
        RequestOptions().override(70).circleCrop()
    }, imageModel = {
        imageUrl
    })
}

@Composable
fun InputMessage(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    sendMessage: () -> Unit
) {
    val newText by rememberUpdatedState(newValue = text)
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        ContentTextField(modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray)
            .weight(1f),
            text = newText,
            onTextChanged = onTextChanged,
            placeholder = { Text(text = "메세지 보내기", color = Color.White) })
        Box(
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    sendMessage()
                }, contentAlignment = Alignment.Center
        ) {
            Icon(
                tint = Color.White,
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_send_24),
                contentDescription = ""
            )
        }
    }
}