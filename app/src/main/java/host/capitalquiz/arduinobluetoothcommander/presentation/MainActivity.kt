package host.capitalquiz.arduinobluetoothcommander.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsWithResult ->
            val canEnableBT = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionsWithResult[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if (canEnableBT && viewModel.isBlueToothEnabled().not()) {
                enableBluetoothLauncher.launch(null)
            }
        }

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )

        if (viewModel.isBlueToothEnabled().not()) {
            enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        setContent {
            ArduinoBluetoothCommanderTheme {
                val uiState by viewModel.state.collectAsState()

                LaunchedEffect(key1 = uiState.errorMessage) {
                    uiState.errorMessage?.let { message ->
                        Toast.makeText(
                            applicationContext,
                            message,
                            Toast.LENGTH_LONG
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
                                makeDiscoverableOverBluetoothLauncher.launch(/* ms */300)
                            },
                            onDeviceClick = viewModel::connectTo
                        )
                }
            }
        }
    }
}
