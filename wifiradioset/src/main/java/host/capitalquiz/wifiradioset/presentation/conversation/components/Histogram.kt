package host.capitalquiz.wifiradioset.presentation.conversation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp

@Composable
fun Histogram(
    values: List<Int>,
    binWidth: Dp = Dp.Unspecified,
    binFactor: Float = 0.8f,
    color: Color,
    symmetric: Boolean = false,
    flip: Boolean = false,
    modifier: Modifier,
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val items = if (flip) values.reversed() else values
            val middleY = size.height / 2
            val middleIndex = values.size shr 1
            val step = size.width / values.size
            val strokeWidth = if (binWidth == Dp.Unspecified) step * binFactor else binWidth.toPx()
            var left = step / 2
            for (i in items.indices) {
                val half =
                    (if (symmetric && i > middleIndex) items[middleIndex - (i - middleIndex)] else items[i]) / 2
                drawLine(
                    start = Offset(x = left, y = middleY + half.coerceAtLeast(1)),
                    end = Offset(x = left, y = middleY - half.coerceAtLeast(1)),
                    color = color,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                left += step
            }
        }
    }
}