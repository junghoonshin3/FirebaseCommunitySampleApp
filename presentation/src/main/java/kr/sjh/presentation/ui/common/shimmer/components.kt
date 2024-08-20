package kr.sjh.presentation.ui.common.shimmer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.DefaultTintBlendMode

fun Modifier.shimmerLoadingAnimation(
    isLoading: Boolean = true,
    backgroundColorAfterLoading: Color = Color.Transparent,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
): Modifier {
    return if (isLoading) {
        composed {
            val shimmerColors = ShimmerAnimationData().getColours()

            val transition = rememberInfiniteTransition(label = "")

            val translateAnimation = transition.animateFloat(
                initialValue = 0f,
                targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = durationMillis,
                        easing = LinearEasing,
                    ),
                    repeatMode = RepeatMode.Restart,
                ),
                label = "Shimmer loading animation",
            )

            this.background(
                brush = Brush.linearGradient(
                    colors = shimmerColors,
                    start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                    end = Offset(x = translateAnimation.value, y = angleOfAxisY),
                )
            )
        }
    } else {
        this.background(backgroundColorAfterLoading)
    }
}

data class ShimmerAnimationData(
    private val isLightMode: Boolean = true
) {
    fun getColours(): List<Color> {
        val color = Color.Gray
        return listOf(
            color.copy(alpha = 0.3f),
            color.copy(alpha = 0.5f),
            color.copy(alpha = 1.0f),
            color.copy(alpha = 0.5f),
            color.copy(alpha = 0.3f),
        )
        //TODO 테마 변경시 참고할 예제 코드
//        return if (isLightMode) {
//            val color = Color.Gray
//
//            listOf(
//                color.copy(alpha = 0.3f),
//                color.copy(alpha = 0.5f),
//                color.copy(alpha = 1.0f),
//                color.copy(alpha = 0.5f),
//                color.copy(alpha = 0.3f),
//            )
//        } else {
//            val color = Color.Black
//
//            listOf(
//                color.copy(alpha = 0.0f),
//                color.copy(alpha = 0.3f),
//                color.copy(alpha = 0.5f),
//                color.copy(alpha = 0.3f),
//                color.copy(alpha = 0.0f),
//            )
//        }
    }
}
