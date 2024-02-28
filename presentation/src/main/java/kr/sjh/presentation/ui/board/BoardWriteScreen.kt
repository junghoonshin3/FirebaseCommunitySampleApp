package kr.sjh.presentation.ui.board

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.sjh.presentation.R
import kr.sjh.presentation.ui.theme.backgroundColor

@Composable
fun BoardWriteScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    LazyColumn(modifier = modifier.background(backgroundColor)) {
        item {
            BoardWriteTopBar(Modifier.fillMaxWidth(), onBack)
        }
        item {
            PictureAdd()
        }
    }
}

@Composable
fun BoardWriteTopBar(
    modifier: Modifier = Modifier, onBack: () -> Unit
) {
    Box(
        modifier = modifier
            .background(backgroundColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Image(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    onBack()
                },
            imageVector = Icons.Default.ArrowBack,
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = "Back"
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(text = "음식점 후기 올리기", color = Color.White)
        }
    }
}

@Composable
fun PictureAdd() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .border(BorderStroke(1.dp, Color(0xFFC1C7CD)), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center

    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
            contentDescription = ""
        )
    }
}