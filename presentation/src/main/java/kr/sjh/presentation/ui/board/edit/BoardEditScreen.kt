package kr.sjh.presentation.ui.board.edit

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kr.sjh.presentation.ui.board.write.BoardWriteBody
import kr.sjh.presentation.ui.board.write.BoardWritePicture
import kr.sjh.presentation.ui.common.AppTopBar
import kr.sjh.presentation.ui.theme.backgroundColor

@Composable
fun BoardEditRoute(
    modifier: Modifier = Modifier,
    boardEditViewModel: BoardEditViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var selectedPhotos by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    val scrollState = rememberScrollState()

    val uiState by boardEditViewModel.editUiState.collectAsStateWithLifecycle()

    Box {
        when (uiState) {
            is BoardEditUiState.Error -> {}
            BoardEditUiState.Loading -> {}
            is BoardEditUiState.Success -> {
                val post = (uiState as BoardEditUiState.Success).post
                BoardEditScreen(
                    modifier = modifier,
                    onUpdate = {
                        boardEditViewModel.updatePost(post)
                        onBack()
                    },
                    onBack = onBack,
                    selectedPhotos = selectedPhotos,
                    onSelectedPhotos = {
                        Log.d("sjh", "picture : ${it.size}")
                        selectedPhotos = it
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

                    )
            }
        }

    }

}

@Composable
private fun BoardEditScreen(
    modifier: Modifier = Modifier,
    selectedPhotos: List<Uri>,
    title: String,
    content: String,
    scrollState: ScrollState,
    onSelectedPhotos: (List<Uri>) -> Unit,
    updateContent: (String) -> Unit,
    updateTitle: (String) -> Unit,
    onUpdate: () -> Unit,
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
                    .weight(1f),
                selectedPhotos = selectedPhotos,
                title = title,
                content = content,
                updateContent = updateContent,
                updateTitle = updateTitle,
                scrollState = scrollState
            )
            BoardWritePicture(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .imePadding(),
                onPhoto = onSelectedPhotos
            )
        }
    }
}

