package host.capitalquiz.common.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun LocalUserCornerShape(radius: Dp = 16.dp) = RoundedCornerShape(
    topStart = radius,
    topEnd = radius,
    bottomStart = radius,
    bottomEnd = 0.dp
)

fun OtherUserCornerShape(radius: Dp = 16.dp) = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = radius,
    bottomStart = radius,
    bottomEnd = radius
)

fun CloudShape(radius: Dp = 16.dp) = LocalUserCornerShape(radius)


class SpeechBubble(
    private val tailLength: Dp = 16.dp,
    private val cornerRadius: Dp = 16.dp,
    private val isRight: Boolean = false,
    private val tailShift: Dp = Dp.Unspecified,
    private val tailRadius: Dp = 0.dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val left = with(density) { tailLength.toPx() }
        val radius = with(density) { cornerRadius.toPx() }
        val tipRadius = with(density) { tailRadius.toPx() }
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = left,
                    top = 0f,
                    right = size.width,
                    bottom = size.height,
                    radiusX = radius,
                    radiusY = radius
                )
            )

            val archShiftFactor = 0.5f
            val deltaY =
                if (tailShift == Dp.Unspecified) 2 * radius else with(density) { tailShift.toPx() }
            val y = size.height - deltaY
            moveTo(left, y)
            val endX = 0f + tipRadius
            val endY = size.height - tipRadius
            cubicTo(left, y + deltaY * archShiftFactor, left * archShiftFactor, endY, endX, endY)
            if (tipRadius > 0) {
                val handleX = 0.2f
                cubicTo(handleX, endY, handleX, size.height, tipRadius, size.height)
            }
            val x2 = left + radius / 2
            val y2 = size.height - radius / 2
            quadraticBezierTo(x2, y2 + radius * 0.3f, x2, y2)
            close()
        }

        if (isRight) {
            val matrix = Matrix()
            val bounds = path.getBounds()
            matrix.translate(bounds.width, 0f)
            matrix.scale(-1f, 1f)
            path.transform(matrix)
        }

        return Outline.Generic(path)
    }
}
