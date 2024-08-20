package kr.sjh.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.theme.carrot

@Composable
fun ProfileImage(
    modifier: Modifier = Modifier,
    imageModel: Any?,
) {
    Box(modifier = modifier) {
        SubcomposeAsyncImage(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .align(Alignment.Center),
            model = imageModel,
            loading = {
                Box(modifier = Modifier.matchParentSize()) {
                    CircularProgressIndicator(
                        color = carrot, modifier = Modifier.align(Alignment.Center)
                    )
                }
            },
            contentDescription = null,
        )
        Image(
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(35.dp),
            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
            contentDescription = ""
        )
    }
}