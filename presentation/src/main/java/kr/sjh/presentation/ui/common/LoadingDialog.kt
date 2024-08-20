package kr.sjh.presentation.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kr.sjh.presentation.ui.theme.carrot

@Composable
fun LoadingDialog() {
    Dialog(
        onDismissRequest = { /*TODO*/ }, properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            decorFitsSystemWindows = false,
            usePlatformDefaultWidth = false
        )
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp), color = carrot, strokeWidth = 5.dp
        )
    }
}