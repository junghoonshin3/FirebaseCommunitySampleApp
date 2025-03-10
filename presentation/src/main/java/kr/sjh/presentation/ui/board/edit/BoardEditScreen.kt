package kr.sjh.presentation.ui.board.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kr.sjh.presentation.ui.board.write.BoardWriteBody
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.common.BoardPicture
import kr.sjh.presentation.ui.common.LoadingDialog
import kr.sjh.presentation.ui.theme.backgroundColor
import kr.sjh.presentation.utill.addFocusCleaner

@Composable
fun BoardEditRoute(
    modifier: Modifier = Modifier,
    boardEditViewModel: BoardEditViewModel = hiltViewModel(),
    navigateToDetail: (String) -> Unit,
    onBack: () -> Unit
) {

    val editUiState by boardEditViewModel.editUiState.collectAsStateWithLifecycle()

    BoardEditScreen(modifier = modifier, editUiState = editUiState, onUpdate = {
        boardEditViewModel.updatePost(editUiState.post)
    }, onBack = onBack, updateContent = {
        boardEditViewModel.updateContent(it)
    }, updateTitle = {
        boardEditViewModel.updateTitle(it)
    }, onDelete = {
        boardEditViewModel.removeSelectedImage(it)
    }, navigateToDetail = navigateToDetail, onPhoto = {
        boardEditViewModel.setSelectedImages(it)
    })

}

@Composable
private fun BoardEditScreen(
    modifier: Modifier = Modifier,
    editUiState: EditUiState,
    onPhoto: (List<String>) -> Unit,
    updateContent: (String) -> Unit,
    updateTitle: (String) -> Unit,
    onDelete: (String) -> Unit,
    onUpdate: () -> Unit,
    onBack: () -> Unit,
    navigateToDetail: (String) -> Unit,
) {

    val scrollState = rememberScrollState()
    val snackBarState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = editUiState.isComplete, block = {
        if (editUiState.isComplete) {
            navigateToDetail(editUiState.post.postKey)
        }
    })

    if (editUiState.loading) {
        LoadingDialog()
    }

    Box(modifier = modifier
        .background(backgroundColor)
        .addFocusCleaner(focusManager)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppTopBar(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                title = "음식점 후기글 쓰기",
                buttonTitle = "수정",
                onBack = onBack,
                backIcon = Icons.Default.ArrowBack,
                onClick = onUpdate
            )
            BoardWriteBody(
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f),
                selectedImages = editUiState.post.images,
                title = editUiState.post.title,
                content = editUiState.post.content,
                updateContent = updateContent,
                updateTitle = updateTitle,
                scrollState = scrollState,
                focusManager = focusManager,
                onDelete = onDelete
            )
            BoardPicture(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .imePadding(),
                onPhoto = {
                    if ((editUiState.post.images.size + it.size) > 3) {
                        coroutineScope.launch {
                            snackBarState.showSnackbar(
                                "사진은 최대 3장까지 첨부 할 수 있어요", "확인", duration = SnackbarDuration.Short
                            )
                        }
                    } else {
                        onPhoto(it)
                    }

                },
            )

        }
        SnackbarHost(
            hostState = snackBarState, modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

