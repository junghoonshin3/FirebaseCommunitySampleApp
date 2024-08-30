package kr.sjh.presentation.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.presentation.ui.common.CenterPullToRefreshContainer
import kr.sjh.presentation.ui.common.InfinityLazyColumn
import kr.sjh.presentation.ui.common.RefreshingType
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.calculationTime
import kr.sjh.presentation.utill.clickableSingle
import java.util.Date

@Composable
fun ChatRoute(
    bottomBar: @Composable () -> Unit,
    navigateToDetail: (String, String, String) -> Unit,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val chatRoomUiState by chatViewModel.chatRooms.collectAsStateWithLifecycle()

    val isRefreshing by chatViewModel.isRefreshing.collectAsStateWithLifecycle(RefreshingType.NONE)

    Scaffold(bottomBar = bottomBar) {
        ChatScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            chatRoomUiState = chatRoomUiState,
            isRefreshing = isRefreshing,
            navigateToDetail = navigateToDetail,
            onRefresh = chatViewModel::refreshChatRooms,
            onRemove = chatViewModel::removeChatRoom
        )
    }

}

@Composable
private fun ChatScreen(
    modifier: Modifier = Modifier,
    chatRoomUiState: ChatRoomUiState,
    isRefreshing: RefreshingType,
    navigateToDetail: (String, String, String) -> Unit,
    onRefresh: () -> Unit,
    onRemove: (ChatRoomModel) -> Unit
) {

    Box(
        modifier = modifier.background(backgroundColor)
    ) {
        ChatRoomList(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            chatRooms = chatRoomUiState.rooms,
            isLoading = chatRoomUiState.isLoading,
            isRefreshing = isRefreshing,
            navigateToDetail = navigateToDetail,
            onRefresh = onRefresh,
            onRemove = onRemove
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomList(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    chatRooms: List<ChatRoomModel>,
    isRefreshing: RefreshingType,
    navigateToDetail: (String, String, String) -> Unit,
    onRefresh: () -> Unit,
    onRemove: (ChatRoomModel) -> Unit
) {
    val pullToRefreshState =
        rememberPullToRefreshState(positionalThreshold = 50.dp, enabled = { true })

    val lazyListState = rememberLazyListState()

    LaunchedEffect(isRefreshing) {
        when (isRefreshing) {
            RefreshingType.NONE -> {}
            RefreshingType.START -> {}
            RefreshingType.END -> {
                delay(500)
                pullToRefreshState.endRefresh()
                lazyListState.animateScrollToItem(0)
            }
        }
    }

    Box(
        modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        if (chatRooms.isEmpty()) {
            Text(
                modifier = Modifier
                    .offset(y = pullToRefreshState.verticalOffset.dp)
                    .align(Alignment.Center),
                text = "채팅방이 비어있어요",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        InfinityLazyColumn(lazyListState = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .offset(y = pullToRefreshState.verticalOffset.dp),
            loadMore = { }) {
            itemsIndexed(chatRooms, key = { _, room -> room.roomId }) { index, room ->
                val you = room.you
                val nickname = you.nickName
                val profileUrl = you.profileImageUrl
                val unReadCount = room.unReadMessageCount
                ChatRoom(modifier = Modifier
                    .clickableSingle {
                        navigateToDetail(
                            room.roomId, nickname, profileUrl
                        )
                    }
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(backgroundColor),
                    isLoading = isLoading,
                    recentMessage = room.recentMessage,
                    timeStamp = calculationTime(
                        room.recentMessageTimeStamp?.time ?: Date().time
                    ),
                    nickname = nickname,
                    profileUrl = profileUrl,
                    unReadCount = unReadCount,
                    onRemove = {
                        onRemove(room)
                    })

            }
        }

        CenterPullToRefreshContainer(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            pullToRefreshState = pullToRefreshState,
            onRefresh = onRefresh
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoom(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    profileUrl: String = "",
    nickname: String = "",
    unReadCount: Long = 0L,
    recentMessage: String = "",
    timeStamp: String = "",
    onRemove: () -> Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when (it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    false
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove()
                    true
                }

                SwipeToDismissBoxValue.Settled -> {
                    false
                }
            }
        },
        positionalThreshold = { it * 0.7f },
    )

    if (swipeToDismissBoxState.currentValue != SwipeToDismissBoxValue.StartToEnd) {
        LaunchedEffect(Unit) {
            swipeToDismissBoxState.reset()
        }
    }

    SwipeToDismissBox(state = swipeToDismissBoxState, backgroundContent = {
        DismissBackground(swipeToDismissBoxState)
    }, enableDismissFromStartToEnd = false) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(5.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                model = profileUrl,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(backgroundColor),
                verticalArrangement = Arrangement.Center
            ) {
                Text(color = Color.White, fontSize = 18.sp, text = nickname)
                Text(
                    color = Color.White,
                    fontSize = 20.sp,
                    text = recentMessage,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(color = Color.White, fontSize = 16.sp, text = timeStamp)
            }
            if (unReadCount > 0) {
                BadgeCount(
                    Modifier
                        .padding(end = 5.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(carrot),
                    unReadCount
                )
            }
        }
    }
}

@Stable
@Composable
fun BadgeCount(modifier: Modifier = Modifier, count: Long, textSize: TextUnit = 15.sp) {

    val messageCount by remember(count) {
        derivedStateOf {
            if (count > 999) {
                "999+"
            } else {
                "$count"
            }
        }
    }

    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(5.dp),
            text = messageCount,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = textSize
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> backgroundColor
        SwipeToDismissBoxValue.EndToStart -> carrot
        SwipeToDismissBoxValue.Settled -> backgroundColor

    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            modifier = Modifier.padding(end = 10.dp), text = "나가기", style = TextStyle(
                fontSize = 20.sp, fontWeight = FontWeight.Bold
            ), color = Color.White
        )
    }
}