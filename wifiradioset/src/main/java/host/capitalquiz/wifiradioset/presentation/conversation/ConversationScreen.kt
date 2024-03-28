package host.capitalquiz.wifiradioset.presentation.conversation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import host.capitalquiz.common.SingleEventEffect
import host.capitalquiz.wifiradioset.R
import host.capitalquiz.wifiradioset.presentation.conversation.contracts.RequestMicPermission

@Composable
fun ConversationScreen(viewModel: ConversationViewModel, onDisconnect: () -> Unit) {
    val context = LocalContext.current


    val micPermissionContract =
        rememberLauncherForActivityResult(RequestMicPermission()) { allowRecord ->
            if (allowRecord) {
                viewModel.connect()
            } else {
                onDisconnect()
            }
        }

    var askPermission by rememberSaveable { mutableStateOf(true) }
    if (askPermission) {
        LaunchedEffect(Unit) {
            micPermissionContract.launch()
            askPermission = false
        }
    }

    SingleEventEffect(sideEffectFlow = viewModel.event) { event ->
        event
            .message { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            .navigate(onDisconnect)
            .audioSessionReady(viewModel::startAudioVisualization)
            .connectionReady(viewModel::startConversation)
    }

    BackHandler(enabled = true, onBack = onDisconnect)

    val interactionSource = remember { MutableInteractionSource() }
    val isSpeakButtonPressed by interactionSource.collectIsPressedAsState()

    val histogramValue by viewModel.frequencies.collectAsState()

    LaunchedEffect(isSpeakButtonPressed) {
        if (isSpeakButtonPressed) {
            viewModel.speak()
        } else {
            viewModel.listen()
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            RoundButton(
                icon = painterResource(R.drawable.ic_speaker_on_24),
                iconSize = 32.dp,
                modifier = Modifier.size(48.dp),
                contentDescription = stringResource(R.string.enable_speakerphone),
                padding = 4.dp,
                tint = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    val inputStream =
                        context.resources.openRawResource(R.raw.walkie_talkie_beep_44khz)
                    viewModel.sendSignal(inputStream)
                }
            )
        }
        Histogram(
            values = histogramValue,
            color = MaterialTheme.colorScheme.secondary,
            symmetric = false,
            flip = false,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .weight(1f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            RoundButton(
                icon = painterResource(R.drawable.ic_microphone_24),
                iconSize = 100.dp,
                modifier = Modifier.size(100.dp),
                contentDescription = stringResource(R.string.speak_button),
                interactionSource = interactionSource
            )
        }
    }
}

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

@Preview(name = "Circle Button")
@Composable
fun LayoutPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            RoundButton(
                icon = painterResource(R.drawable.ic_speaker_on_24),
                iconSize = 32.dp,
                modifier = Modifier.size(48.dp),
                contentDescription = stringResource(R.string.enable_speakerphone),
                padding = 4.dp,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        Histogram(
            values = (23..300).step(5).take(64),
            4.dp,
            color = Color.Green,
            flip = false,
            symmetric = true,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize()
                .weight(1f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            RoundButton(
                icon = painterResource(R.drawable.ic_microphone_24),
                iconSize = 100.dp,
                modifier = Modifier.size(100.dp),
                contentDescription = stringResource(R.string.speak_button),
            )
        }
    }
}

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
            val step = size.width / (values.size - 1)
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