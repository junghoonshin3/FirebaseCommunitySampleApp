package kr.sjh.presentation.ui.board.detail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kotlin.math.absoluteValue


val COLLAPSED_TOP_BAR_HEIGHT = 50.dp
val EXPANDED_TOP_BAR_HEIGHT = 400.dp

@Composable
fun BoardDetailRoute(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    detailViewModel: BoardDetailViewModel = hiltViewModel(),
) {
    val listState = rememberLazyListState()

    val overlapHeightPx = with(LocalDensity.current) {
        EXPANDED_TOP_BAR_HEIGHT.toPx() - COLLAPSED_TOP_BAR_HEIGHT.toPx()
    }

    val isCollapsed by remember {
        derivedStateOf {
            val isFirstItemHidden =
                listState.firstVisibleItemScrollOffset > overlapHeightPx

            isFirstItemHidden || listState.firstVisibleItemIndex > 0
        }
    }

    val color by animateColorAsState(
        if (isCollapsed) {
            backgroundColor
        } else {
            Color.Transparent
        }, label = ""
    )

    val writeUiState by detailViewModel.writeUiState.collectAsStateWithLifecycle()

    val isLike by remember {
        mutableStateOf(false)
    }


    BoardDetailScreen(
        modifier = modifier,
        isCollapsed = isCollapsed,
        isLike = isLike,
        writeUiState = writeUiState,
        listState = listState,
        onBack = onBack,
        color = color,
        onLikeChange = {

        }
    )

}

@Composable
fun BoardDetailScreen(
    modifier: Modifier = Modifier,
    writeUiState: WriteUiState,
    color: Color,
    isCollapsed: Boolean,
    isLike: Boolean,
    listState: LazyListState,
    onBack: () -> Unit,
    onLikeChange: (Boolean) -> Unit,
) {

    Box(modifier = modifier) {
        //접힌 상태 탑바
        when (writeUiState) {
            is WriteUiState.Error -> {

            }

            WriteUiState.Loading -> {
                CircularProgressIndicator(
                    color = carrot,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is WriteUiState.Success -> {
                DetailCollapsedTopBar(
                    modifier = Modifier
                        .zIndex(1f)
                        .fillMaxWidth()
                        .height(COLLAPSED_TOP_BAR_HEIGHT),
                    isCollapsed = isCollapsed,
                    isLike = isLike,
                    color = color,
                    onLikeChange = { like ->
                        onLikeChange(like)
                    },
                    onBack = onBack
                )
                LazyColumn(
                    modifier = Modifier
                        .background(backgroundColor)
                        .fillMaxSize(), state = listState
                ) {
                    item {
                        //확장된 상태 탑바
                        DetailExpendedTopBar(
                            Modifier
                                .fillMaxWidth()
                                .height(EXPANDED_TOP_BAR_HEIGHT)
                        )
                    }
                    item {
                        DetailWriterProfile(
                            profileImageUrl = writeUiState.userInfo.profileImageUrl,
                            nickName = writeUiState.userInfo.nickName ?: "닉네임이 없어요",
                            readCount = 0,
                            postCount = writeUiState.userInfo.postCount
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            thickness = 1.dp,
                            color = Color.Gray
                        )
                        DetailTitle(
                            modifier = Modifier.padding(10.dp),
                            title = writeUiState.post.title ?: ""
                        )
                        DetailContent(
                            Modifier.padding(10.dp),
                            content = writeUiState.post.content ?: ""
                        )

                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailExpendedTopBar(modifier: Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {
        val pagerState = rememberPagerState {
            5
        }
        HorizontalPager(state = pagerState) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.test_image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        Indicator(
            5, pagerState,
            Modifier
                .height(20.dp)
        )
    }
}


@Composable
fun DetailCollapsedTopBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean,
    isLike: Boolean,
    color: Color,
    onLikeChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {

    Row(
        modifier = modifier
            .background(color),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    onBack()
                },
            colorFilter = ColorFilter.tint(
                if (isCollapsed) {
                    Color.White
                } else {
                    Color.Black
                }
            ),
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back"
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
//                    isLike = !isLike
                    onLikeChange(isLike)
                },
            colorFilter = ColorFilter.tint(
                if (isLike) {
                    carrot
                } else if (isCollapsed) {
                    Color.White
                } else {
                    Color.Black
                }
            ),
            imageVector = ImageVector.vectorResource(
                id = if (isLike) {
                    R.drawable.baseline_favorite_24
                } else {
                    R.drawable.baseline_favorite_border_24
                }
            ),
            contentDescription = "Like"
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Indicator(
    count: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = modifier
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(count) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                )
            }
        }
        Box(
            Modifier
                .jumpingDotTransition(pagerState, 0.8f)
                .size(8.dp)
                .background(
                    color = Color(0xFFE78111),
                    shape = CircleShape
                )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.jumpingDotTransition(pagerState: PagerState, jumpScale: Float) =
    graphicsLayer {
        val pageOffset = pagerState.currentPageOffsetFraction
        val scrollPosition = pagerState.currentPage + pageOffset
        translationX =
            scrollPosition * (size.width + 8.dp.roundToPx()) // 8.dp - spacing between dots

        val scale: Float
        val targetScale = jumpScale - 1f

        scale = if (pageOffset.absoluteValue < .5) {
            1.0f + (pageOffset.absoluteValue * 2) * targetScale;
        } else {
            jumpScale + ((1 - (pageOffset.absoluteValue * 2)) * targetScale);
        }

        scaleX = scale
        scaleY = scale
    }

@Composable
fun DetailWriterProfile(
    profileImageUrl: String?,
    nickName: String,
    readCount: Int,
    postCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        GlideImage(
            modifier = Modifier.size(80.dp),
            imageModel = {
                profileImageUrl ?: R.drawable.baseline_face_24
            },
            requestOptions = {
                RequestOptions()
                    .override(
                        with(LocalDensity.current) { 80.dp.toPx() }.toInt(),
                        with(LocalDensity.current) { 80.dp.toPx() }.toInt()
                    )
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
            },
            loading = {
                Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        color = carrot,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = nickName, color = Color.White, fontSize = 20.sp)
            Text(text = "내가 쓴 글 $postCount", color = Color.Gray)
            Text(text = "조회수 $readCount", color = Color.Gray)
        }
    }
}

@Composable
fun DetailTitle(modifier: Modifier = Modifier, title: String) {
    Text(modifier = modifier, text = title, fontSize = 30.sp, color = Color.White)
}

@Composable
fun DetailContent(modifier: Modifier = Modifier, content: String) {
    Text(
        modifier = modifier,
        text = content,
        fontSize = 20.sp,
        color = Color.White
    )
}