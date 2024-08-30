package kr.sjh.presentation.ui.board.image

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.theme.carrot
import kr.sjh.presentation.utill.clickableSingle
import kr.sjh.presentation.utill.optimizedBitmap

@Composable
fun SelectedImages(
    modifier: Modifier = Modifier, imageUrls: List<String> = emptyList(), onDelete: (String) -> Unit
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(imageUrls) { url ->
            Picture(url, onDelete)
        }
    }
}

@Composable
private fun Picture(imageUri: String, onDelete: (String) -> Unit) {

    Box(modifier = Modifier.background(Color.Transparent, RoundedCornerShape(10.dp))) {
        SubcomposeAsyncImage(
            modifier = Modifier.aspectRatio(1f),
            model = imageUri,
            loading = {
                Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        color = carrot, modifier = Modifier.align(Alignment.Center)
                    )
                }
            }, contentScale = ContentScale.Fit, contentDescription = null
        )
        Image(
            modifier = Modifier
                .size(25.dp)
                .align(Alignment.TopEnd)
                .clickableSingle {
                    onDelete(imageUri)
                },
            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_cancel_24),
            contentDescription = ""
        )
    }

}
