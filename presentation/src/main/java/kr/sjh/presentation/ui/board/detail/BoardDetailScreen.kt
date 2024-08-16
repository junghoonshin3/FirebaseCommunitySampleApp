package kr.sjh.presentation.ui.board.detail

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import kr.sjh.domain.constant.Role
import kr.sjh.domain.model.UserModel
import kr.sjh.domain.util.generateUniqueChatKey
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.common.CommonModalBottomSheet
import kr.sjh.presentation.ui.common.CommonPopUp
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.common.popup.TwoButtonPopUpContent
import kr.sjh.presentation.ui.main.MainViewModel
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.getActivity
import kr.sjh.presentation.utill.jumpingDotTransition


val COLLAPSED_TOP_BAR_HEIGHT = 50.dp
val EXPANDED_TOP_BAR_HEIGHT = 400.dp

@Composable
fun BoardDetailRoute(
    modifier: Modifier = Modifier,
    detailViewModel: BoardDetailViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel(getActivity()),
    onBack: () -> Unit,
    onChat: (String, String, String) -> Unit,
    onEdit: (String) -> Unit,
) {

    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()

    val user by mainViewModel.currentUser.collectAsStateWithLifecycle()

    BoardDetailScreen(modifier = modifier,
        user = user,
        detailUiState = detailUiState,
        onBack = onBack,
        onMoreMenu = detailViewModel::setBottomSheetVisible,
        onLikeChange = {},
        onChat = onChat,
        onDismissRequest = detailViewModel::setBottomSheetVisible,
        onEdit = {
            onEdit(it)
        },
        onDelete = {
            detailViewModel.deletePost {
                onBack()
            }
        },
        onHide = {
            detailViewModel.hidePost {
                onBack()
            }
        },
        onBan = {
            detailViewModel.banUser {
                onBack()
            }
        })

}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
@Composable
fun BoardDetailScreen(
    modifier: Modifier = Modifier,
    user: UserModel,
    onDismissRequest: () -> Unit,
    detailUiState: DetailUiState,
    onBack: () -> Unit,
    onMoreMenu: () -> Unit,
    onLikeChange: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
    onChat: (String, String, String) -> Unit,
    onHide: () -> Unit,
    onBan: (String) -> Unit
) {

    val listState = rememberLazyListState()

    val overlapHeightPx = with(LocalDensity.current) {
        EXPANDED_TOP_BAR_HEIGHT.toPx() - COLLAPSED_TOP_BAR_HEIGHT.toPx()
    }

    val isCollapsed by remember {
        derivedStateOf {
            val isFirstItemHidden = listState.firstVisibleItemScrollOffset > overlapHeightPx

            isFirstItemHidden || listState.firstVisibleItemIndex > 0
        }
    }

    val bottomSheetItems = mutableListOf<BottomSheetItem>().apply {
        if (user.role == Role.ADMIN) {
            add(BottomSheetItem.Edit)
            add(BottomSheetItem.Delete)
            add(BottomSheetItem.Report)
            add(BottomSheetItem.Ban)
        } else if (user.uid == detailUiState.writerUser.uid) {
            add(BottomSheetItem.Edit)
            add(BottomSheetItem.Delete)
        } else {
            add(BottomSheetItem.Report)
            add(BottomSheetItem.Ban)
        }
    }

    val configuration = LocalConfiguration.current

    var isPopUp by remember {
        mutableStateOf(false)
    }

    if (detailUiState.loading) {
        LoadingDialog()
//        BoardDetailShimmer()
        return
    }
    if (isPopUp) {
        CommonPopUp(content = {
            TwoButtonPopUpContent(title = "이 사용자의 글 보지 않기",
                subTitle = "${detailUiState.writerUser.nickName}님의 모든 게시글을 보시지 않으시겠어요?",
                onConfirm = { onBan(detailUiState.writerUser.uid) },
                onCancel = {
                    isPopUp = false
                })
        })
    }


    CommonModalBottomSheet(
        showSheet = detailUiState.bottomSheetShow, dragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.LightGray)
        }, containerColor = backgroundColor, onDismissRequest = onDismissRequest
    ) {
        BottomSheetMoreMenu(modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor),
            items = bottomSheetItems,
            onClick = { item ->
                when (item) {
                    BottomSheetItem.Ban -> {
                        isPopUp = true
                    }

                    BottomSheetItem.Delete -> {
                        onDelete()
                    }

                    BottomSheetItem.Edit -> {
                        onEdit(detailUiState.post.postKey)
                    }

                    BottomSheetItem.Report -> {
                        onHide()
                    }
                }
            })
    }

    Box(modifier = modifier) {
        DetailCollapsedTopBar(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .height(COLLAPSED_TOP_BAR_HEIGHT),
            isCollapsed = isCollapsed,
            isLike = false,
            onLikeChange = onLikeChange,
            onMoreMenu = onMoreMenu,
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
                        .height(EXPANDED_TOP_BAR_HEIGHT),
                    images = detailUiState.post.images
                )
            }
            item {
                DetailWriterProfile(
                    profileImageUrl = detailUiState.writerUser.profileImageUrl,
                    nickName = detailUiState.writerUser.nickName,
                    readCount = detailUiState.post.readCount,
                    postCount = detailUiState.writerUser.myPosts.size
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray
                )
                DetailTitle(
                    modifier = Modifier.padding(10.dp), title = detailUiState.post.title ?: ""
                )
                DetailContent(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .heightIn(min = configuration.screenHeightDp.dp - EXPANDED_TOP_BAR_HEIGHT),
                    content = detailUiState.post.content
                )
                if (user.uid != detailUiState.writerUser.uid) {
                    val roomId = generateUniqueChatKey(
                        user.uid, detailUiState.writerUser.uid
                    )
                    DetailRequestChat(modifier = Modifier
                        .size(100.dp, 60.dp)
                        .padding(10.dp)
                        .background(carrot, RoundedCornerShape(5.dp)), onChat = {
                        onChat(
                            roomId,
                            detailUiState.writerUser.nickName,
                            detailUiState.writerUser.profileImageUrl
                        )
                    })
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailExpendedTopBar(modifier: Modifier, images: List<String>) {

    val context = LocalContext.current
    Box(
        modifier = modifier, contentAlignment = Alignment.BottomCenter
    ) {

        val pageCount by remember(images) {
            mutableIntStateOf(
                if (images.isEmpty()) {
                    1
                } else {
                    images.size
                }
            )
        }

        val pagerState = rememberPagerState(initialPage = 0) {
            pageCount
        }

        HorizontalPager(state = pagerState) { index ->
            SubcomposeAsyncImage(model = if (images.isEmpty()) {
                R.drawable.test_image
            } else {
                images[index]
            }, modifier = Modifier.fillMaxSize(), loading = {
                Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center), color = carrot
                    )
                }
            }, error = {
                it.result.throwable.printStackTrace()
                Image(
                    modifier = Modifier.size(50.dp),
                    colorFilter = ColorFilter.tint(Color.White),
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_image_not_supported_24),
                    contentDescription = ""
                )
            }, contentDescription = null
            )
        }
        Indicator(
            pageCount, pagerState, Modifier.height(20.dp)
        )
    }
}


@Composable
fun DetailCollapsedTopBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean,
    isLike: Boolean,
    onLikeChange: () -> Unit,
    onMoreMenu: () -> Unit,
    onBack: () -> Unit,
) {
    val collapseColor by remember(isCollapsed) {
        derivedStateOf {
            if (isCollapsed) {
                Color.White
            } else {
                Color.Black
            }
        }
    }

    val collapseBackgroundColor by remember(isCollapsed) {
        derivedStateOf {
            if (isCollapsed) {
                backgroundColor
            } else {
                Color.Transparent
            }
        }
    }

    val likeColor by remember(isLike, isCollapsed) {
        derivedStateOf {
            if (isLike) {
                carrot
            } else if (isCollapsed) {
                Color.White
            } else {
                Color.Black
            }
        }
    }

    Row(
        modifier = modifier.background(collapseBackgroundColor),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(50.dp),
            content = {
                Icon(
                    imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
                )
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = collapseColor),
            onClick = onBack
        )

        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.size(50.dp),
            content = {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = if (isLike) {
                            R.drawable.baseline_favorite_24
                        } else {
                            R.drawable.baseline_favorite_border_24
                        }
                    ), contentDescription = "Like"
                )
            },
            colors = IconButtonDefaults.iconButtonColors(contentColor = likeColor),
            onClick = onLikeChange
        )
        IconButton(
            onClick = onMoreMenu,
            colors = IconButtonDefaults.iconButtonColors(contentColor = collapseColor)
        ) {
            Icon(
                modifier = Modifier.padding(10.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_more_vert_24),
                contentDescription = ""
            )
        }
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
        modifier = modifier, contentAlignment = Alignment.CenterStart
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = modifier.height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(count) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = Color.White, shape = CircleShape
                        )
                )
            }
        }
        Box(
            Modifier
                .jumpingDotTransition(pagerState, 0.8f)
                .size(8.dp)
                .background(
                    color = Color(0xFFE78111), shape = CircleShape
                )
        )
    }
}

@Composable
fun DetailWriterProfile(
    profileImageUrl: String?, nickName: String, readCount: Int, postCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape), loading = {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center), color = carrot
                )
            }, model = profileImageUrl ?: R.drawable.baseline_face_24, contentDescription = null
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
        modifier = modifier, text = content, fontSize = 20.sp, color = Color.White
    )
}

@Composable
fun DetailRequestChat(modifier: Modifier = Modifier, onChat: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = modifier.clickable { onChat() }, contentAlignment = Alignment.Center
        ) {
            Text(fontSize = 15.sp, color = Color.White, text = "채팅하기")
        }
    }
}