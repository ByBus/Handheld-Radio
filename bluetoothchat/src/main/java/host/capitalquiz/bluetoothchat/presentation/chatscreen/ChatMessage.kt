package host.capitalquiz.bluetoothchat.presentation.chatscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import host.capitalquiz.common.ui.DarkPurple
import host.capitalquiz.common.ui.DarkerGreen
import host.capitalquiz.common.ui.SemiWhite
import host.capitalquiz.common.ui.SpeechBubble
import host.capitalquiz.common.ui.theme.ArduinoBluetoothCommanderTheme

@Composable
fun ChatMessage(
    message: MessageUi,
    modifier: Modifier = Modifier,
) {
    val clipShape = speechBubble(message.fromMe)
    val bgColor = if (message.fromMe) DarkPurple else DarkerGreen
    val align = if (message.fromMe) Alignment.End else Alignment.Start
    val margin = if (message.fromMe) PaddingValues(start = 32.dp) else PaddingValues(end = 32.dp)
    val gravity = if (message.fromMe) Arrangement.End else Arrangement.Start
    val innerPadding =
        if (message.fromMe) PaddingValues(start = 8.dp, end = 18.dp, bottom = 2.dp, top = 4.dp)
        else PaddingValues(start = 18.dp, end = 8.dp, bottom = 2.dp, top = 4.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 1.dp),
        horizontalArrangement = gravity
    ) {
        Column(
            modifier = Modifier
                .padding(margin)
                .shadow(elevation = 1.dp, shape = clipShape)
                .background(bgColor)
                .width(IntrinsicSize.Max)
                .defaultMinSize(minWidth = 200.dp)
                .padding(innerPadding),
        ) {
//            Text(text = message.name, fontSize = 11.sp, color = nickColor)
            Text(
                text = message.text,
                color = Color.White,
                modifier = Modifier
                    .widthIn(max = 250.dp)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 1.dp), thickness = 0.5.dp, color = SemiWhite
            )
            Text(
                text = message.date,
                fontSize = 11.sp,
                color = SemiWhite,
                modifier = Modifier
                    .align(align)
                    .padding(start = 4.dp, end = 2.dp)
            )
        }
    }
}

@Preview
@Composable
fun SpeechBubblePreview() {
    ArduinoBluetoothCommanderTheme {
        Column {
            ChatMessage(
                message = MessageUi(
                    1L, "Redmi Note 12S", "Hello World!", "Today", 1L, false
                )
            )
            ChatMessage(
                message = MessageUi(
                    2L, "Xperia Z1", "Hello World!", "Today", 1L, true
                )
            )
        }
    }
}

@Composable
private fun speechBubble(isFromMe: Boolean) = SpeechBubble(
    tailLength = 10.dp,
    cornerRadius = 16.dp,
    tailRadius = 1.dp,
    tailShift = 24.dp,
    isRight = isFromMe
)

