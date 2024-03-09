package host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen

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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import host.capitalquiz.arduinobluetoothcommander.ui.theme.ArduinoBluetoothCommanderTheme
import host.capitalquiz.arduinobluetoothcommander.ui.theme.DarkerGreen
import host.capitalquiz.arduinobluetoothcommander.ui.theme.LightGreen
import host.capitalquiz.arduinobluetoothcommander.ui.theme.LocalUserCornerShape
import host.capitalquiz.arduinobluetoothcommander.ui.theme.OtherUserCornerShape
import host.capitalquiz.arduinobluetoothcommander.ui.theme.SemiWhite

@Composable
fun ChatMessage(
    message: MessageUi,
    modifier: Modifier = Modifier,
) {
    val clipShape = if (message.fromMe) LocalUserCornerShape() else OtherUserCornerShape()
    val bgColor = if (message.fromMe) LightGreen else DarkerGreen
    val align = if (message.fromMe) Alignment.End else Alignment.Start
    val margin = if (message.fromMe) PaddingValues(start = 32.dp) else PaddingValues(end = 32.dp)
    val gravity = if (message.fromMe) Arrangement.End else Arrangement.Start
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = gravity
    ) {
        Column(
            modifier = Modifier
                .padding(margin)
                .shadow(elevation = 2.dp, shape = clipShape)
                .background(bgColor)
                .width(IntrinsicSize.Max)
                .defaultMinSize(minWidth = 200.dp)
                .padding(start = 8.dp, end = 8.dp, bottom = 2.dp, top = 6.dp),
        ) {
            Text(text = message.name, fontSize = 11.sp, color = Color.DarkGray)
            Text(
                text = message.text, color = Color.DarkGray,
                modifier = Modifier
                    .widthIn(max = 250.dp)
                    .padding(horizontal = 8.dp)
            )
            Divider(
                color = SemiWhite,
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 2.dp)
            )
            Text(
                text = message.date, fontSize = 11.sp, color = SemiWhite,
                modifier = Modifier
                    .align(align)
                    .padding(start = 4.dp)
            )
        }
    }
}


@Preview
@Composable
fun ChatWithMessagePreview() {
    ArduinoBluetoothCommanderTheme {
        ChatMessage(
            message = MessageUi(
                id = 1,
                name = "Nokia 3310",
                text = "Some message text",
                date = "Пт 27.12.2024 15:34",
                fromMe = true,
                chatId = 0
            )
        )
    }
}

@Preview
@Composable
fun ChatWithMessageFromMePreview() {
    ArduinoBluetoothCommanderTheme {
        ChatMessage(
            message = MessageUi(
                id = 1,
                name = "Nokia 3310",
                text = "Some long message text and more words to show",
                date = "Пт 27.12.2024 15:34",
                fromMe = false,
                chatId = 0
            )
        )
    }
}