package kr.sjh.presentation.ui.board.edit

import android.net.Uri
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kr.sjh.presentation.ui.board.write.BoardWriteBody
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.common.BoardPicture
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.theme.backgroundColor

@Composable
fun BoardEditRoute(
    modifier: Modifier = Modifier,
    boardEditViewModel: BoardEditViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val scrollState = rememberScrollState()

    val post by boardEditViewModel.post.collectAsStateWithLifecycle()

    val editUiState by boardEditViewModel.editUiState.collectAsStateWithLifecycle()

    val snackBarState = remember { SnackbarHostState() }

    val selectedImages = remember(post) {
        mutableStateListOf<Uri>().apply {
            addAll(post.images.map { Uri.parse(it) })
        }
    }

    val coroutineScope = rememberCoroutineScope()

    when (editUiState) {
        is BoardEditUiState.Error -> {}
        BoardEditUiState.Loading -> {
            LoadingDialog()
        }

        is BoardEditUiState.Success -> {
            onBack()
        }

        BoardEditUiState.Init -> {}
    }

    Box(modifier = modifier.background(backgroundColor)) {
        BoardEditScreen(
            modifier = modifier,
            onUpdate = {
                boardEditViewModel.updatePost(
                    post.copy(
                        images = selectedImages.map { it.toString() }
                    )
                )
            },
            onBack = onBack,
            selectedPhotos = selectedImages,
            onPhoto = {
                if ((selectedImages.size + it.size) > 3) {
                    coroutineScope.launch {
                        snackBarState.showSnackbar(
                            "사진은 최대 3장까지 첨부 할 수 있어요",
                            "확인",
                            duration = SnackbarDuration.Short
                        )
                    }
                    return@BoardEditScreen
                }
                selectedImages.addAll(it)
            },
            scrollState = scrollState,
            title = boardEditViewModel.title,
            content = boardEditViewModel.content,
            updateContent = {
                boardEditViewModel.updateContent(it)
            },
            updateTitle = {
                boardEditViewModel.updateTitle(it)
            },
            onDelete = {
                selectedImages.remove(it)
            }
        )
        SnackbarHost(hostState = snackBarState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun BoardEditScreen(
    modifier: Modifier = Modifier,
    selectedPhotos: List<Uri>,
    title: String,
    content: String,
    scrollState: ScrollState,
    onPhoto: (List<Uri>) -> Unit,
    updateContent: (String) -> Unit,
    updateTitle: (String) -> Unit,
    onUpdate: () -> Unit,
    onDelete: (Uri) -> Unit,
    onBack: () -> Unit,
) {

    Surface(
        modifier = modifier,
        contentColor = backgroundColor,
        color = backgroundColor
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AppTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                title = "음식점 후기글 쓰기",
                buttonTitle = "수정",
                onBack = onBack,
                backIcon = Icons.Default.ArrowBack,
                onClick = onUpdate
            )
            BoardWriteBody(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                selectedImages = selectedPhotos,
                title = title,
                content = content,
                updateContent = updateContent,
                updateTitle = updateTitle,
                scrollState = scrollState,
                onDelete = onDelete
            )
            BoardPicture(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .imePadding(),
                onPhoto = onPhoto
            )
        }
    }
}

