package kr.sjh.presentation.ui.board

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.CenterPullToRefreshContainer
import kr.sjh.presentation.ui.common.InfinityLazyColumn
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.common.RefreshingType
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.calculationTime
import kr.sjh.presentation.utill.clickableSingle

@Composable
fun BoardRoute(
    navigateToBoardDetail: (String) -> Unit,
    navigateToBoardWrite: () -> Unit,
    bottomBar: @Composable () -> Unit,
    boardViewModel: BoardViewModel = hiltViewModel()
) {
    val boardUiState by boardViewModel.postUiState.collectAsStateWithLifecycle()

    val isRefreshing by boardViewModel.isRefreshing.collectAsStateWithLifecycle(initialValue = RefreshingType.NONE)

    Scaffold(bottomBar = bottomBar) {
        BoardScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            boardUiState = boardUiState,
            isRefreshing = isRefreshing,
            navigateToBoardDetail = { postKey ->
                boardViewModel.updatePostCount(postKey)
                navigateToBoardDetail(postKey)
            },
            onRefresh = boardViewModel::refreshPosts,
            nextPosts = boardViewModel::nextPosts,
            navigateToBoardWrite = navigateToBoardWrite
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    modifier: Modifier = Modifier,
    boardUiState: BoardUiState,
    isRefreshing: RefreshingType,
    onRefresh: () -> Unit,
    nextPosts: () -> Unit,
    navigateToBoardDetail: (String) -> Unit,
    navigateToBoardWrite: () -> Unit
) {

    val lazyListState = rememberLazyListState()

    val pullToRefreshState = rememberPullToRefreshState(positionalThreshold = 50.dp, enabled = {
        true
    })

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
        modifier = modifier
            .background(backgroundColor)
            .nestedScroll(pullToRefreshState.nestedScrollConnection)
    ) {
        if (boardUiState.isLoading) {
            LoadingDialog()
            return
        }

        if (boardUiState.posts.isEmpty()) {
            Text(
                text = "텅! 글쓰기를 해볼까요?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .offset(
                        y = pullToRefreshState.verticalOffset.dp
                    )
                    .align(Alignment.Center)
            )
        }

        InfinityLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = pullToRefreshState.verticalOffset.dp),
            lazyListState = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
            loadMore = nextPosts
        ) {
            itemsIndexed(boardUiState.posts, key = { _, post -> post.postKey }) { index, post ->
                PostItem(modifier = Modifier
                    .fillMaxWidth()
                    .clickableSingle {
                        navigateToBoardDetail(post.postKey)
                    }
                    .padding(10.dp),
                    title = post.title,
                    nickname = post.nickName,
                    createAt = post.timeStamp.time,
                    readCount = post.readCount,
                    likeCount = post.likeCount,
                    images = post.images)
            }
        }

        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .zIndex(1f),
            shape = RoundedCornerShape(30.dp),
            containerColor = carrot,
            text = {
                Text(
                    text = "글쓰기", color = Color.White
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "create",
                    tint = Color.White
                )
            },
            onClick = navigateToBoardWrite
        )

        CenterPullToRefreshContainer(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            pullToRefreshState = pullToRefreshState,
            onRefresh = onRefresh
        )
    }
}

@Composable
fun PostItem(
    modifier: Modifier = Modifier,
    title: String,
    images: List<String>,
    nickname: String,
    createAt: Long,
    readCount: Int,
    likeCount: Int
) {
    val minutesAgo by remember {
        mutableStateOf(calculationTime(createAt))
    }

    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            model = if (images.isEmpty()) {
                R.drawable.baseline_image_24
            } else {
                images.first()
            },
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp)),
            loading = {
                Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        color = carrot, modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.Center)
                    )
                }
            },
            error = {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_image_not_supported_24),
                    contentDescription = ""
                )
                R.drawable.baseline_image_not_supported_24
            },
            contentDescription = "",
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 17.sp,
                maxLines = 1
            )
            Text(
                text = nickname,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                text = minutesAgo,
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                text = "조회수 $readCount",
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
            Text(
                text = "관심 $likeCount",
                color = Color.White,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                maxLines = 1
            )
        }
    }
}

