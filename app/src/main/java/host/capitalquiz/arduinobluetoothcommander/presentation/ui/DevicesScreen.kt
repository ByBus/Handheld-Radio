package host.capitalquiz.arduinobluetoothcommander.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import host.capitalquiz.arduinobluetoothcommander.R
import host.capitalquiz.arduinobluetoothcommander.presentation.BluetoothUiState

@Composable
fun DevicesScreen(
    state: BluetoothUiState,
    onStartSearch: () -> Unit,
    onStopSearch: () -> Unit,
    onStartServer: () -> Unit,
    onDeviceClick: (DeviceUi) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DevicesList(
            pairedDevices = state.pairedDevices,
            foundDevices = state.scannedDevices,
            onClickDevice = onDeviceClick,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(onClick = onStartSearch) {
                Text(text = stringResource(R.string.start_scan))
            }
            Button(onClick = onStopSearch) {
                Text(text = stringResource(R.string.stop_scan))
            }
            Button(onClick = onStartServer) {
                Text(text = stringResource(R.string.start_server))
            }
        }
    }
}

@Composable
fun DevicesList(
    pairedDevices: List<DeviceUi>,
    foundDevices: List<DeviceUi>,
    onClickDevice: (DeviceUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item {
            DevicesListTitle(stringResource(R.string.paired_devices))
        }
        items(pairedDevices) { pairedDevice ->
            DeviceItem(device = pairedDevice, onClickDevice = onClickDevice)
        }
        item {
            DevicesListTitle(stringResource(R.string.scanned_devices))
        }
        items(foundDevices) { foundDevice ->
            DeviceItem(device = foundDevice, onClickDevice = onClickDevice)
        }
    }
}

@Composable
private fun DevicesListTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun DeviceItem(device: DeviceUi, onClickDevice: (DeviceUi) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable { onClickDevice(device) })
    {
        Text(text = device.name, fontSize = 18.sp)
        Text(text = device.macAddress, color = MaterialTheme.colorScheme.secondary)
    }
}