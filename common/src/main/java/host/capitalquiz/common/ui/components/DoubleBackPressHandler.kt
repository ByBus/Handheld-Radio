package host.capitalquiz.common.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun DoubleBackPressHandler(
    delayMs: Long = 2000,
    onFirstPress: () -> Unit,
    onSecondPress: () -> Unit,
) {
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(delayMs)
            isPressed = false
        }
    }

    BackHandler(enabled = true) {
        if (isPressed.not()) {
            onFirstPress.invoke()
            isPressed = true
        } else {
            onSecondPress.invoke()
        }
    }
}