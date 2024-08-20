package kr.sjh.presentation.ui.board.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.PersistentList

@Composable
fun BottomSheetMoreMenu(
    modifier: Modifier = Modifier,
    items: List<BottomSheetItem>,
    onClick: (BottomSheetItem) -> Unit
) {

    LazyColumn(
        modifier = modifier, verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(items) { index, item ->
            BottomSheetMenu(
                onClick = onClick,
                bottomSheetItem = item,
            )
            if (items.size - 1 > index) {
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun BottomSheetMenu(
    bottomSheetItem: BottomSheetItem,
    onClick: (BottomSheetItem) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onClick(bottomSheetItem)
            }, contentAlignment = Alignment.Center
    ) {
        Text(
            text = bottomSheetItem.title,
            fontWeight = FontWeight.Bold,
            fontSize = bottomSheetItem.fontSize,
            color = bottomSheetItem.color
        )

    }
}

@Immutable
sealed class BottomSheetItem(
    val title: String, val color: Color, val fontSize: TextUnit = 17.sp
) {
    data object Edit : BottomSheetItem("수정하기", Color.White, 17.sp)
    data object Delete : BottomSheetItem("삭제하기", Color.White, 17.sp)
    data object Report : BottomSheetItem("신고", Color.Red, 17.sp)
    data object Ban : BottomSheetItem("이 회원의 글 보지 않기", Color.Red, 17.sp)
}