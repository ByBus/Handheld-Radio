package host.capitalquiz.wifiradioset.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import host.capitalquiz.wifiradioset.R
import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.presentation.contracts.RequestWifiPermissions

@Composable
fun WiFiRadioSetScreen(viewModel: WiFiRadioSetViewModel, onBack: () -> Unit) {
    val startDevicesDiscoveryLauncher =
        rememberLauncherForActivityResult(RequestWifiPermissions()) { allowDiscovery ->
            if (allowDiscovery) viewModel.findDevices()
        }
    val devices by viewModel.devices.collectAsState()
    val uiState by viewModel.wifiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = uiState.toString())
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { startDevicesDiscoveryLauncher.launch() }) {
                Text(text = stringResource(R.string.find_devices))
            }
        }

        LazyColumn() {
            items(devices) { item ->
                DeviceItem(device = item, onClickDevice = viewModel::connect)
            }
        }
    }
}


@Composable
fun DeviceItem(device: WifiDevice, onClickDevice: (WifiDevice) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 6.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.walkie_talkie_24),
            contentDescription = "",
            tint = Color.Unspecified,
            modifier = Modifier.size(48.dp)
        )
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClickDevice(device) })
        {
            Text(text = device.name, fontSize = 18.sp)
            Text(text = device.address, color = MaterialTheme.colorScheme.secondary)
        }
    }
}