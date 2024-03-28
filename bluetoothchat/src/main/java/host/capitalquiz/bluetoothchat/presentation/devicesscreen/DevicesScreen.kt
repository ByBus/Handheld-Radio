package host.capitalquiz.bluetoothchat.presentation.devicesscreen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import host.capitalquiz.bluetoothchat.R
import host.capitalquiz.bluetoothchat.components.TimedProgressBar
import host.capitalquiz.bluetoothchat.presentation.devicesscreen.contracts.EnableBluetoothContract
import host.capitalquiz.bluetoothchat.presentation.devicesscreen.contracts.MakeDiscoverableOverBluetoothContract
import host.capitalquiz.bluetoothchat.presentation.devicesscreen.contracts.RequestAllBluetoothPermissionsContract

@Composable
fun DevicesScreen(
    viewModel: BluetoothViewModel,
    onNavigateToChat: (deviceName: String, mac: String) -> Unit,
) {
    val serverName = stringResource(R.string.server_name)
    val makeDiscoverableOverBluetoothLauncher =
        rememberLauncherForActivityResult(MakeDiscoverableOverBluetoothContract()) { allowed ->
            if (allowed) viewModel.startServer(serverName)
        }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        EnableBluetoothContract()
    ) {}

    val permissionLauncher = rememberLauncherForActivityResult(
        RequestAllBluetoothPermissionsContract()
    ) { canEnableBT ->
        if (canEnableBT) enableBluetoothLauncher.launch()
    }

    SideEffect {
        permissionLauncher.launch()
    }

    val context = LocalContext.current
    val uiState by viewModel.state.collectAsState()
    val messageEvent by viewModel.message.collectAsState()

    LaunchedEffect(key1 = messageEvent) {
        messageEvent.consume { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(key1 = uiState.isConnected) {
        if (uiState.isConnected) uiState.deviceData(onNavigateToChat::invoke)
    }

    BackHandler(enabled = uiState.isConnecting) {
        viewModel.disconnect()
    }
    AnimatedContent(
        targetState = uiState.isConnecting,
        label = "Connecting or devices"
    ) { isConnecting ->
        if (isConnecting) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.please_wait))
                TimedProgressBar(
                    duration = uiState.showProgressDuration,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                )
            }
        } else {
            val visibilityDuration = integerResource(R.integer.bluetooth_visible_to_others_duration)
            DevicesList(
                state = uiState,
                onStartSearch = viewModel::startScanning,
                onStopSearch = viewModel::stopScanning,
                onStartServer = {
                    makeDiscoverableOverBluetoothLauncher.launch(/* ms */visibilityDuration)
                },
                onDeviceClick = viewModel::connectTo
            )
        }
    }
}