package host.capitalquiz.arduinobluetoothcommander.presentation

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DevicesScreen
import host.capitalquiz.arduinobluetoothcommander.ui.theme.ArduinoBluetoothCommanderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel by viewModels<BluetoothViewModel>()

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

        }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsWithResult ->
            val canEnableBT = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionsWithResult[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if (canEnableBT && viewModel.isBlueToothEnabled().not()) {
                enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }

        if (viewModel.isBlueToothEnabled().not()) {
            enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        setContent {
            ArduinoBluetoothCommanderTheme {
                val uiState by viewModel.state.collectAsState()

                DevicesScreen(
                    state = uiState,
                    onStartSearch = viewModel::startScanning,
                    onStopSearch = viewModel::stopScanning
                )
            }
        }
    }
}
