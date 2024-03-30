package host.capitalquiz.wifiradioset.presentation.conversation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RoundButton(
    icon: Painter,
    iconSize: Dp,
    modifier: Modifier,
    contentDescription: String,
    onClick: () -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    padding: Dp = 12.dp,
    tint: Color = Color.Unspecified,
) {
    Button(
        shape = CircleShape,
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(padding),
        interactionSource = interactionSource
    ) {
        Icon(
            painter = icon,
            modifier = Modifier.size(iconSize),
            tint = tint,
            contentDescription = contentDescription
        )
    }
}