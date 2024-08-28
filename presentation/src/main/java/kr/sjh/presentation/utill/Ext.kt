package kr.sjh.presentation.utill

import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.sjh.presentation.navigation.RootScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.absoluteValue


fun Modifier.addFocusCleaner(focusManager: FocusManager, doOnClear: () -> Unit = {}): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            doOnClear()
            focusManager.clearFocus()
        })
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.jumpingDotTransition(pagerState: PagerState, jumpScale: Float) = graphicsLayer {
    val pageOffset = pagerState.currentPageOffsetFraction
    val scrollPosition = pagerState.currentPage + pageOffset
    translationX = scrollPosition * (size.width + 8.dp.roundToPx()) // 8.dp - spacing between dots

    val scale: Float
    val targetScale = jumpScale - 1f

    scale = if (pageOffset.absoluteValue < .5) {
        1.0f + (pageOffset.absoluteValue * 2) * targetScale;
    } else {
        jumpScale + ((1 - (pageOffset.absoluteValue * 2)) * targetScale);
    }

    scaleX = scale
    scaleY = scale
}

@Composable
fun getActivity() = LocalContext.current as ComponentActivity


@Stable
@Composable
fun NavController.currentRootScreenAsState(): State<RootScreen> {
    val selectedItem = remember {
        mutableStateOf<RootScreen>(
            RootScreen.Board
        )
    }

    DisposableEffect(key1 = this) {
        val listener = NavController.OnDestinationChangedListener { navController, destination, _ ->
            when {
                destination.hierarchy.any {
                    it.route == RootScreen.Board.route
                } -> {
                    selectedItem.value = RootScreen.Board
                }

                destination.hierarchy.any { it.route == RootScreen.Chat.route } -> {
                    selectedItem.value = RootScreen.Chat
                }

                destination.hierarchy.any { it.route == RootScreen.MyPage.route } -> {
                    selectedItem.value = RootScreen.MyPage
                }
            }
        }
        addOnDestinationChangedListener(listener)
        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }
    return selectedItem
}


fun NavController.navigateToRootScreen(rootScreen: RootScreen) {
    navigate(rootScreen.route) {
        popUpTo(graph.id) {
            saveState = true
        }
        restoreState = true
        launchSingleTop = true
    }

}

fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    indication: @Composable () -> Indication? = { LocalIndication.current },
    onClick: () -> Unit
) = then(composed(inspectorInfo = debugInspectorInfo {
    name = "clickable"
    properties["enabled"] = enabled
    properties["onClickLabel"] = onClickLabel
    properties["role"] = role
    properties["onClick"] = onClick
}) {

    var duplicated by remember { mutableStateOf(false) }

    val timer = rememberCoroutineScope()

    Modifier.clickable(enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        indication = indication(),
        interactionSource = remember { MutableInteractionSource() }) {
        if (duplicated) return@clickable

        duplicated = true

        onClick()
        timer.launch {
            delay(500)
            duplicated = false
        }
    }
})

fun String.toEncodingURL(): String {
    return URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
}
