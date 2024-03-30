package host.capitalquiz.wifiradioset.presentation.devices

import host.capitalquiz.common.presentation.ResourceProvider
import host.capitalquiz.wifiradioset.R
import host.capitalquiz.wifiradioset.domain.WifiState
import javax.inject.Inject

class WifiStateUiMapper @Inject constructor(
    private val stringProvider: ResourceProvider<String>,
) : WifiState.Mapper<WifiStateUi> {
    override fun invoke(wifiState: WifiState): WifiStateUi {
        return when (wifiState) {
            WifiState.Idle -> WifiStateUi.Idle
            is WifiState.Connected -> WifiStateUi.Connected(wifiState.device)
            WifiState.ConnectionFailed -> WifiStateUi.ConnectionFailed(String(R.string.connection_was_rejected))
            is WifiState.DevicesFound -> WifiStateUi.DevicesFound(wifiState.wifiDevices)
            WifiState.Disconnected -> WifiStateUi.Disconnected(String(R.string.device_was_disconnected))
            WifiState.Off -> WifiStateUi.Off
            WifiState.On -> WifiStateUi.On
        }
    }

    private fun String(id: Int): String = stringProvider.provide(id)
}