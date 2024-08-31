package kr.sjh.presentation.ui.common

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kr.sjh.presentation.ui.theme.carrot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterPullToRefreshContainer(
    modifier: Modifier = Modifier, pullToRefreshState: PullToRefreshState, onRefresh: () -> Unit
) {

    val scaleTransition = updateTransition(
        targetState = pullToRefreshState.verticalOffset.div(100),  // 이 상태값이 변하면 transition 작동
        label = "scaleTransition"
    )

    val scale by scaleTransition.animateFloat(label = "scale") { state ->
        state.coerceIn(0f, 1f)
    }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            onRefresh()
        }
    }

    Box(
        modifier = modifier.graphicsLayer {
            translationY = pullToRefreshState.verticalOffset - size.center.y
        }, contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = pullToRefreshState,
            label = "",
            animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
        ) { state ->
            if (state.isRefreshing) {
                CircularProgressIndicator(
                    strokeWidth = 5.dp,
                    color = carrot,
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.scale(scale),
                    strokeWidth = 5.dp,
                    color = carrot,
                    progress = { pullToRefreshState.progress })
            }
        }


    }
}