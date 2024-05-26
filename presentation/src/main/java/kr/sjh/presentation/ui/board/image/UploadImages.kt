package kr.sjh.presentation.ui.board.image

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UploadImages(uri: List<Uri>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(uri) {

        }
    }
}
