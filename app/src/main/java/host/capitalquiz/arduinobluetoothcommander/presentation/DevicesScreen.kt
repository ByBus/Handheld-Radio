package host.capitalquiz.arduinobluetoothcommander.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
import host.capitalquiz.arduinobluetoothcommander.R
import host.capitalquiz.arduinobluetoothcommander.presentation.contracts.EnableBluetoothContract
import host.capitalquiz.arduinobluetoothcommander.presentation.contracts.MakeDiscoverableOverBluetoothContract
import host.capitalquiz.arduinobluetoothcommander.presentation.contracts.RequestAllBluetoothPermissionsContract
import host.capitalquiz.arduinobluetoothcommander.presentation.contracts.launch
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DevicesList

@Composable
fun DevicesScreen(
    viewModel: BluetoothViewModel = hiltViewModel(),
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

    when {
        uiState.isConnecting -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(text = stringResource(R.string.please_wait))
            }
        }

        else -> {
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