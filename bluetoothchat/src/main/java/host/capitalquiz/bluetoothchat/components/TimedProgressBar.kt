package host.capitalquiz.bluetoothchat.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun TimedProgressBar(duration: Int, modifier: Modifier = Modifier) {
    var oldValue by rememberSaveable { mutableFloatStateOf(1f) }
    var isRunning by rememberSaveable { mutableStateOf(false) }

    var currentProgress by remember { mutableFloatStateOf(if (isRunning) oldValue else 1f) }
    val updatedDuration by remember {
        mutableIntStateOf(if (isRunning) (duration * oldValue).roundToInt() else duration)
    }

    var currentHeight by remember { mutableStateOf(10.dp) }

    val progressAnimation by animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = tween(
            durationMillis = updatedDuration,
            easing = FastOutSlowInEasing
        ),
        label = "timed indicator progress",
        finishedListener = { isRunning = false }
    )

    SideEffect {
        oldValue = progressAnimation
    }

    val height by animateDpAsState(
        targetValue = currentHeight,
        animationSpec = tween(durationMillis = updatedDuration.toInt()),
        label = "timed indicator height"
    )

    LaunchedEffect(Unit) {
        currentProgress = 0f
        isRunning = true
        currentHeight = Dp.Hairline
    }

    Row(modifier) {
        LinearProgressIndicator(
            progress = progressAnimation,
            modifier = Modifier
                .weight(1f)
                .height(height)
                .graphicsLayer { rotationZ = 180f }
        )
        LinearProgressIndicator(
            progress = progressAnimation,
            modifier = Modifier
                .weight(1f)
                .height(height)
        )
    }
}