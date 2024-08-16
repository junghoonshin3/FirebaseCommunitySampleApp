package kr.sjh.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CommonPopUp(
    content: @Composable () -> Unit, properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        usePlatformDefaultWidth = true,
        decorFitsSystemWindows = true
    )
) {
    Dialog(
        onDismissRequest = { }, properties = properties
    ) {
        content()
    }
}