package kr.sjh.presentation.ui.common

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ContentTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (text: String) -> Unit,
    singleLine: Boolean = false,
    placeholder: @Composable () -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = TextStyle.Default,
    parentScrollState: ScrollState? = null
) {
    val coroutineScope = rememberCoroutineScope()
    var prevHeight by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(3.dp))
            .padding(10.dp),
    ) {
        BasicTextField(
            singleLine = singleLine,
            modifier = modifier
                .onSizeChanged { size ->
                    parentScrollState?.let {
                        //변경된 텍스트 필드의 높이 - 변경되기 전 높이
                        val diff = size.height - prevHeight
                        prevHeight = size.height
                        if (prevHeight == 0 || diff == 0) {
                            return@onSizeChanged
                        }
                        coroutineScope.launch {
                            // 줄바꿈시 스크롤을 마지막으로 이동
                            it.scrollTo(
                                it.maxValue
                            )
                        }
                    }
                },
            value = text,
            onValueChange = { text ->
                onTextChanged(text)
            },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = textStyle,
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    placeholder()
                }
                innerTextField()
            }
        )
    }
}
