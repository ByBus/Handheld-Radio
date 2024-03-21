package host.capitalquiz.wifiradioset.presentation.devices

import host.capitalquiz.wifiradioset.domain.WifiDevice

data class RadioSetUiState(
    val isWiFiEnabled: Boolean = false,
    val devices: List<WifiDevice> = emptyList(),
)
