package kr.sjh.presentation.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kr.sjh.domain.model.ChatRoomModel
import kr.sjh.presentation.ui.common.InfinityLazyColumn
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.common.shimmer.shimmerLoadingAnimation
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

    Scaffold(bottomBar = bottomBar) {
        ChatScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            chatRoomUiState = chatRoomUiState,
            navigateToDetail = navigateToDetail,
            onRefresh = chatViewModel::refreshChatRooms
        )
    }

}

@Composable
private fun ChatScreen(
    modifier: Modifier = Modifier,
    chatRoomUiState: ChatRoomUiState,
    navigateToDetail: (String, String, String) -> Unit,
    onRefresh: () -> Unit
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
            isRefreshing = chatRoomUiState.isRefreshing,
            navigateToDetail = navigateToDetail,
            onRefresh = onRefresh
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomList(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    chatRooms: List<ChatRoomModel>,
    isRefreshing: Boolean,
    navigateToDetail: (String, String, String) -> Unit,
    onRefresh: () -> Unit
) {
    val pullToRefreshState =
        rememberPullToRefreshState(positionalThreshold = 30.dp, enabled = { true })

    val lazyListState = rememberLazyListState()

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            pullToRefreshState.startRefresh()
        } else {
            delay(200)
            pullToRefreshState.endRefresh()
        }
    }

    Box(modifier = modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        if (chatRooms.isEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "채팅방이 비어있어요",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        InfinityLazyColumn(lazyListState = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
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
                    .height(100.dp),
                    isLoading = isLoading,
                    recentMessage = room.recentMessage,
                    timeStamp = calculationTime(room.recentMessageTimeStamp?.time ?: Date().time),
                    nickname = nickname,
                    profileUrl = profileUrl,
                    unReadCount = unReadCount)
                if (chatRooms.lastIndex > index) {
                    HorizontalDivider(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp, bottom = 10.dp),
            indicator = { pullRefreshState ->
                if (!pullRefreshState.isRefreshing) {
                    CircularProgressIndicator(
                        strokeWidth = 5.dp,
                        color = carrot,
                        progress = { pullRefreshState.progress })
                } else {
                    CircularProgressIndicator(
                        strokeWidth = 5.dp,
                        color = carrot,
                    )
                }
            },
            containerColor = backgroundColor,
            contentColor = backgroundColor
        )
    }
}


@Composable
fun ChatRoom(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    profileUrl: String = "",
    nickname: String = "",
    unReadCount: Long = 0L,
    recentMessage: String = "",
    timeStamp: String = "",
) {
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
                .background(backgroundColor), verticalArrangement = Arrangement.Center
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
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(carrot), unReadCount
            )
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