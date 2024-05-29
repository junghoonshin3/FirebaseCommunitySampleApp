package kr.sjh.presentation.ui.common

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kr.sjh.presentation.ui.theme.carrot

@Composable
fun LoadingDialog(
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
    )
) {
    Dialog(
        onDismissRequest = { /*TODO*/ },
        properties = properties
    ) {
        CircularProgressIndicator(color = carrot)
    }
}