package kr.sjh.presentation.ui.board.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.sjh.presentation.ui.theme.backgroundColor

@Composable
fun BottomSheetMoreMenu(
    modifier: Modifier = Modifier,
    moveEdit: () -> Unit,
    onDelete: () -> Unit
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "수정",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    moveEdit()
                },
            color = Color.White
        )
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Text(
            textAlign = TextAlign.Center,
            text = "삭제",
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    onDelete()
                },
            color = Color.Red
        )
    }
}