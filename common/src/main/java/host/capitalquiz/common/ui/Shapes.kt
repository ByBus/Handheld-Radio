package values

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
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