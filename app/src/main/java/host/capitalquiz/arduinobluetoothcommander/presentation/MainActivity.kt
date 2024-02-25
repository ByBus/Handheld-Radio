package host.capitalquiz.arduinobluetoothcommander.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import host.capitalquiz.arduinobluetoothcommander.R
import host.capitalquiz.arduinobluetoothcommander.presentation.contracts.EnableBluetoothContract
import host.capitalquiz.arduinobluetoothcommander.presentation.contracts.MakeDiscoverableOverBluetoothContract
import host.capitalquiz.arduinobluetoothcommander.presentation.contracts.RequestAllBluetoothPermissionsContract
import host.capitalquiz.arduinobluetoothcommander.presentation.contracts.launch
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DevicesScreen
import host.capitalquiz.arduinobluetoothcommander.ui.theme.ArduinoBluetoothCommanderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel by viewModels<BluetoothViewModel>()

        val makeDiscoverableOverBluetoothLauncher =
            registerForActivityResult(MakeDiscoverableOverBluetoothContract()) { allowed ->
                if (allowed) viewModel.startServer(getString(R.string.server_name))
            }

        val enableBluetoothLauncher = registerForActivityResult(
            EnableBluetoothContract()
        ) {}

        val permissionLauncher = registerForActivityResult(
            RequestAllBluetoothPermissionsContract()
        ) { canEnableBT ->
            if (canEnableBT) enableBluetoothLauncher.launch()
        }

        permissionLauncher.launch()

        setContent {
            ArduinoBluetoothCommanderTheme {
                val uiState by viewModel.state.collectAsState()
                val messageEvent by viewModel.message.collectAsState()

                LaunchedEffect(key1 = messageEvent) {
                    messageEvent.consume { message ->
                        Toast.makeText(
                            applicationContext,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                LaunchedEffect(key1 = uiState.isConnected) {
                    if (uiState.isConnected) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.devices_are_connected),
                            Toast.LENGTH_LONG
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
                            Text(text = getString(R.string.please_wait))
                        }
                    }

                    else ->
                        DevicesScreen(
                            state = uiState,
                            onStartSearch = viewModel::startScanning,
                            onStopSearch = viewModel::stopScanning,
                            onStartServer = {
                                makeDiscoverableOverBluetoothLauncher.launch(/* ms */
                                    resources.getInteger(R.integer.bluetooth_visible_to_others_duration)
                                )
                            },
                            onDeviceClick = viewModel::connectTo
                        )
                }
            }
        }
    }
}
