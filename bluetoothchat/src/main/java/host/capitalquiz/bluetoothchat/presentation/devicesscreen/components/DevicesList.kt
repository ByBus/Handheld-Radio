package host.capitalquiz.bluetoothchat.presentation.devicesscreen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import host.capitalquiz.bluetoothchat.R
import host.capitalquiz.bluetoothchat.presentation.devicesscreen.BluetoothUiState
import host.capitalquiz.bluetoothchat.presentation.devicesscreen.DeviceUi
import host.capitalquiz.common.ui.RedColor
import host.capitalquiz.common.ui.components.DevicesListTitle
import host.capitalquiz.common.ui.theme.ArduinoBluetoothCommanderTheme

@Composable
fun DevicesList(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            AnimatedContent(
                state.isDiscoveringDevices,
                transitionSpec = {
                    if (state.isDiscoveringDevices) {
                        slideInHorizontally { height -> height } + fadeIn() togetherWith
                                slideOutHorizontally { height -> -height } + fadeOut()
                    } else {
                        slideInHorizontally { height -> -height } + fadeIn() togetherWith
                                slideOutHorizontally { height -> height } + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "Discovering devices buttons",
            ) { isDiscoveringDevices ->
                if (isDiscoveringDevices) {
                    IconButton(
                        text = stringResource(R.string.stop_scan),
                        iconPainter = painterResource(R.drawable.round_bluetooth_disabled_24),
                        colors = ButtonDefaults.buttonColors(containerColor = RedColor),
                        onClick = onStopSearch
                    )
                } else {
                    IconButton(
                        text = stringResource(R.string.start_scan),
                        iconPainter = painterResource(R.drawable.round_bluetooth_searching_24),
                        onClick = onStartSearch
                    )
                }
            }
            IconButton(
                text = stringResource(R.string.start_server),
                iconPainter = painterResource(R.drawable.round_chat_24),
                onClick = onStartServer
            )
        }
    }
}

@Composable
private fun IconButton(
    text: String,
    iconPainter: Painter,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: () -> Unit,
) {
    Button(onClick = onClick, colors = colors, modifier = modifier) {
        Icon(
            painter = iconPainter,
            contentDescription = text,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
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

@Preview(backgroundColor = 0xFFFFFFL, showBackground = true)
@Composable
fun DevicesListTitlePreview() {
    ArduinoBluetoothCommanderTheme {
        Column {
            DevicesListTitle("Подкдюченные устройства")
            DeviceItem(
                device = DeviceUi("Sony Xperia Z1 Compact", "A0:16:F5:24:13"),
                onClickDevice = {})
        }
    }
}

@Composable
fun DeviceItem(device: DeviceUi, onClickDevice: (DeviceUi) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 6.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.round_bluetooth_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.surfaceTint,
            modifier = Modifier.size(48.dp)
        )
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClickDevice(device) })
        {
            Text(text = device.name, fontSize = 18.sp)
            Text(text = device.macAddress, color = MaterialTheme.colorScheme.secondary)
        }
    }
}