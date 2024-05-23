package kr.sjh.presentation.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    title: String,
    buttonTitle: String,
    backIcon: ImageVector,
    onBack: () -> Unit,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable {
                    onBack()
                },
            imageVector = backIcon,
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "Back"
        )
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable {
                    onClick()
                },
            text = buttonTitle,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )

    }
}