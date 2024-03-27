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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import host.capitalquiz.common.ui.DarkerGreen
import host.capitalquiz.common.ui.LightGreen
import host.capitalquiz.common.ui.SemiWhite
import values.LocalUserCornerShape
import values.OtherUserCornerShape

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

