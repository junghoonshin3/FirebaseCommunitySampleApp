package kr.sjh.presentation.ui.board.image

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.theme.carrot

@Composable
fun UploadImages(
    modifier: Modifier = Modifier,
    uris: List<Uri> = emptyList(),
    onDelete: (Uri) -> Unit
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(uris) { uri ->
            Picture(uri, onDelete)
        }
    }
}

@Composable
private fun Picture(uri: Uri, onDelete: (Uri) -> Unit) {
    Box(modifier = Modifier.background(Color.Transparent, RoundedCornerShape(10.dp))) {
        GlideImage(
            modifier = Modifier
                .size(100.dp),
            imageModel = {
                uri
            },
            loading = {
                Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        color = carrot,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        )
        Image(
            modifier = Modifier
                .size(25.dp)
                .align(Alignment.TopEnd)
                .clickable {
                    onDelete(uri)
                },
            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_cancel_24),
            contentDescription = ""
        )
    }

}
