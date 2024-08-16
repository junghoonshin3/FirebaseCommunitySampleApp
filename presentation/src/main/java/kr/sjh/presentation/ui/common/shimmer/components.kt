package kr.sjh.presentation.ui.common.shimmer

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ComponentCircle(
) {
    Box(
        modifier = Modifier
            .background(color = Color.LightGray, shape = CircleShape)
            .clip(CircleShape)
            .size(80.dp)
            .shimmerLoadingAnimation()
    )
}

@Composable
fun ComponentSquare(
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(24.dp))
            .background(color = Color.LightGray)
            .size(80.dp)
            .shimmerLoadingAnimation()
    )
}

@Composable
fun ComponentRectangle(
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(24.dp))
            .background(color = Color.LightGray)
            .fillMaxWidth()
            .aspectRatio(1f)
            .shimmerLoadingAnimation()
    )
}

@Composable
fun ComponentRectangleLineLong(
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.LightGray)
            .size(height = 30.dp, width = 200.dp)
            .shimmerLoadingAnimation()
    )
}

@Composable
fun ComponentRectangleLineShort(
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.LightGray)
            .size(height = 30.dp, width = 100.dp)
            .shimmerLoadingAnimation()
    )
}


fun Modifier.shimmerLoadingAnimation(
    isLoading: Boolean = true, // <-- New parameter for start/stop.
//    isLightModeActive: Boolean = true, // <-- New parameter for display modes.
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
): Modifier {
    return if (isLoading) {
        composed {
            val shimmerColors = ShimmerAnimationData().getColours()

            val transition = rememberInfiniteTransition(label = "")

//            val animatedSize by animateDpAsState(
//                targetValue = 500.dp, animationSpec = infiniteRepeatable(
//                    animation = tween(
//                        durationMillis = durationMillis,
//                        easing = LinearEasing,
//                    ),
//                    repeatMode = RepeatMode.Reverse
//                ),
//                label = "asdf"
//            )

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

            this
//                .width(animatedSize)
                .background(
                    brush = Brush.linearGradient(
                        colors = shimmerColors,
                        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
                    ),
                )
        }
    } else {
        this
    }
}

data class ShimmerAnimationData(
    private val isLightMode: Boolean = true
) {
    fun getColours(): List<Color> {
        return if (isLightMode) {
            val color = Color.White

            listOf(
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 1.0f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 0.3f),
            )
        } else {
            val color = Color.Black

            listOf(
                color.copy(alpha = 0.0f),
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.5f),
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.0f),
            )
        }
    }
}
