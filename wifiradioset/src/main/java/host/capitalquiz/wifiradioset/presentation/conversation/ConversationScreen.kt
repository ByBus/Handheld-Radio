package host.capitalquiz.wifiradioset.presentation.conversation

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import host.capitalquiz.common.SingleEventEffect
import host.capitalquiz.common.ui.components.DoubleBackPressHandler
import host.capitalquiz.common.ui.theme.ArduinoBluetoothCommanderTheme
import host.capitalquiz.wifiradioset.R
import host.capitalquiz.wifiradioset.presentation.conversation.components.Histogram
import host.capitalquiz.wifiradioset.presentation.conversation.components.RoundButton
import host.capitalquiz.wifiradioset.presentation.conversation.contracts.RequestMicPermission

@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel,
    onDisconnect: () -> Unit,
) {
    val context = LocalContext.current

    val micPermissionContract =
        rememberLauncherForActivityResult(RequestMicPermission()) { result ->
            result.check(
                onGranted = viewModel::connect,
                onRejected = { onDisconnect() }
            )
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

    DoubleBackPressHandler(
        onFirstPress = { viewModel.showToast(context.getString(R.string.press_back_button_again_to_leave)) },
        onSecondPress = onDisconnect
    )

    val uiState = viewModel.uiState
    val histogramValues by viewModel.frequencies.collectAsState()
    val interactionSource = remember { MutableInteractionSource() }
    val isSpeakButtonPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isSpeakButtonPressed) {
        if (isSpeakButtonPressed) {
            viewModel.speak()
        } else {
            viewModel.listen()
        }
    }
    val isVerticalOrientation =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    ConversationUi(uiState, histogramValues, interactionSource, isVerticalOrientation) {
        val inputStream = context.resources.openRawResource(R.raw.walkie_talkie_beep_44khz)
        viewModel.sendSignal(inputStream)
    }
}

@Composable
private fun ConversationUi(
    uiState: ConversationUiState,
    histogramValues: List<Int>,
    interactionSource: MutableInteractionSource,
    isVertical: Boolean = true,
    sendSignal: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = if (isVertical) 32.dp else 8.dp)
    ) {
        val (signalButton, histogram, speakButton, deviceName, networkName) = createRefs()
        RoundButton(
            icon = painterResource(R.drawable.ic_speaker_on_24),
            iconSize = 32.dp,
            modifier = Modifier
                .size(48.dp)
                .constrainAs(signalButton) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            contentDescription = stringResource(R.string.enable_speakerphone),
            padding = 4.dp,
            tint = MaterialTheme.colorScheme.onPrimary,
            onClick = sendSignal
        )
        RoundButton(
            icon = painterResource(R.drawable.ic_microphone_24),
            iconSize = 100.dp,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(speakButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            interactionSource = interactionSource,
            contentDescription = stringResource(R.string.speak_button),
        )
        Histogram(
            values = histogramValues,
            color = MaterialTheme.colorScheme.secondary,
            symmetric = false,
            flip = false,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .constrainAs(histogram) {
                    top.linkTo(signalButton.bottom)
                    bottom.linkTo(speakButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(deviceName) {
                start.linkTo(parent.start)
                if (isVertical) {
                    top.linkTo(signalButton.bottom, 16.dp)
                    end.linkTo(parent.end)
                } else {
                    top.linkTo(signalButton.top)
                    end.linkTo(signalButton.start)
                }
            }
        ) {
            Text(
                text = stringResource(R.string.connected_to),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.walkie_talkie_24),
                    contentDescription = uiState.companionName,
                    tint = Color.Unspecified
                )
                Text(
                    text = uiState.companionName,
                    Modifier.padding(top = 6.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(networkName) {

                if (isVertical) {
                    bottom.linkTo(speakButton.top, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                } else {
                    top.linkTo(signalButton.top)
                    end.linkTo(parent.end)
                    start.linkTo(signalButton.end)
                }
            }
        ) {
            Text(
                text = stringResource(R.string.network),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.find_devices),
                    modifier = Modifier.size(18.dp),
                    contentDescription = uiState.networkName,
                    tint = Color.Unspecified
                )
                Text(
                    text = uiState.networkName,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview(name = "Conversation")
@Composable
fun ConversationUiPreview() {
    ArduinoBluetoothCommanderTheme {
        ConversationUi(
            ConversationUiState(
                "Redmi Note 10S",
                "23:a4:7b:d9:f1:4e",
                "DIRECT-5FK-Network"
            ),
            histogramValues = (20..364).shuffled().take(64),
            interactionSource = remember { MutableInteractionSource() },
        ) {}
    }
}

@Preview(
    name = "Conversation Landscape", showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun ConversationUiPreviewLanScape() {
    ArduinoBluetoothCommanderTheme {
        ConversationUi(
            ConversationUiState(
                "Redmi Note 10S",
                "23:a4:7b:d9:f1:4e",
                "DIRECT-5FK-Network"
            ),
            histogramValues = (20..364).shuffled().take(64),
            interactionSource = remember { MutableInteractionSource() },
            isVertical = false
        ) {}
    }
}


