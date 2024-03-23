package host.capitalquiz.wifiradioset.presentation.conversation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import host.capitalquiz.common.SingleEventEffect
import host.capitalquiz.wifiradioset.R
import host.capitalquiz.wifiradioset.presentation.contracts.RequestMicPermission

@Composable
fun ConversationScreen(viewModel: ConversationViewModel, onDisconnect: () -> Unit) {
    val context = LocalContext.current

    SingleEventEffect(sideEffectFlow = viewModel.event) { event ->
        event.message { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            .navigate(onDisconnect)
    }

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

    val interactionSource = remember { MutableInteractionSource() }
    val isSpeakButtonPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isSpeakButtonPressed) {
        if (isSpeakButtonPressed) {
            viewModel.speak()
        } else {
            viewModel.listen()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            shape = CircleShape,
            onClick = { },
            modifier = Modifier.size(100.dp),
            contentPadding = PaddingValues(12.dp),
            interactionSource = interactionSource
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_microphone_24),
                modifier = Modifier.size(100.dp),
                tint = Color.Unspecified,
                contentDescription = stringResource(
                    R.string.speak_button
                )
            )
        }
    }
}

@Preview(name = "Circle Button")
@Composable
fun CircleButton() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            shape = CircleShape,
            onClick = { },
            modifier = Modifier.size(100.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_microphone_24),
                modifier = Modifier.size(100.dp),
                tint = Color.Unspecified,
                contentDescription = stringResource(
                    R.string.speak_button
                )
            )
        }
    }
}