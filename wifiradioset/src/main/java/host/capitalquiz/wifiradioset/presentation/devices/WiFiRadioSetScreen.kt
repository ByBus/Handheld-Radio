package host.capitalquiz.wifiradioset.presentation.devices

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import host.capitalquiz.common.SingleEventEffect
import host.capitalquiz.common.ui.components.DevicesListTitle
import host.capitalquiz.wifiradioset.R
import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.presentation.contracts.RequestWifiPermissions

@Composable
fun WiFiRadioSetScreen(
    viewModel: WiFiRadioSetViewModel,
    shouldDisconnect: Boolean,
    onConnect: () -> Unit,
) {
    val startDevicesDiscoveryLauncher =
        rememberLauncherForActivityResult(RequestWifiPermissions()) { allowDiscovery ->
            if (allowDiscovery) viewModel.findDevices()
        }
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    SingleEventEffect(viewModel.event) { event ->
        event
            .message { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            .navigate(onConnect)
    }

    LaunchedEffect(shouldDisconnect) {
        if (shouldDisconnect) viewModel.disconnect()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(targetState = uiState.devices.isNotEmpty(), label = "wifi devices list") {
            if (it) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn() {
                        item {
                            DevicesListTitle(stringResource(R.string.found_wifi_devices))
                        }
                        items(uiState.devices) { item ->
                            DeviceItem(device = item, onClickDevice = viewModel::connect)
                        }
                    }
                }
            } else {
                FindDevicesScreen { startDevicesDiscoveryLauncher.launch() }
            }
        }
        AnimatedVisibility(uiState.isWiFiEnabled.not()) {
            NoWiFiInfoScreen()
        }
    }
}

@Composable
fun InfoScreen(imageId: Int, message: AnnotatedString, bottomContent: @Composable () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(id = imageId), contentDescription = message.toString())
        Text(
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            text = message
        )
        bottomContent()
    }
}

@Composable
fun FindDevicesScreen(onFindDevice: () -> Unit) {
    val message = stringResource(R.string.start_wifi_discovery)
    val annotatedMessage = buildAnnotatedString { append(message) }
    InfoScreen(imageId = R.drawable.find_devices, message = annotatedMessage) {
        Button(onClick = onFindDevice) {
            Text(text = stringResource(R.string.find_devices))
        }
    }
}

@Composable
fun NoWiFiInfoScreen() {
    val wifiLabel = stringResource(R.string.wifi_label)
    val locationLabel = stringResource(R.string.location_label)
    val message = stringResource(
        R.string.please_enable_wifi_and_location,
        wifiLabel,
        locationLabel
    )
    val annotatedMessage =
        makeAnnotatedString(message = message, labels = listOf(wifiLabel, locationLabel))
    InfoScreen(imageId = R.drawable.wifi_disabled, message = annotatedMessage)
}

@Composable
fun makeAnnotatedString(
    message: String,
    labels: List<String>,
    labelStyle: SpanStyle = SpanStyle(
        color = colorResource(id = R.color.label_green),
        fontWeight = FontWeight.Bold
    ),
): AnnotatedString {
    return buildAnnotatedString {
        append(message)
        for (label in labels) {
            val startIndex = message.indexOf(label)
            addStyle(labelStyle, startIndex, startIndex + label.length)
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