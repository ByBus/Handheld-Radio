package host.capitalquiz.wifiradioset.presentation.devices

import host.capitalquiz.wifiradioset.domain.WifiDevice

sealed interface WifiStateUi {
    fun reduce(uiState: RadioSetUiState): RadioSetUiState
    fun produceEvent(): Event

    abstract class BaseWiFiUiState : WifiStateUi {
        protected open val enabled = true
        override fun produceEvent(): Event = Event.Empty

        override fun reduce(uiState: RadioSetUiState): RadioSetUiState =
            uiState.copy(isWiFiEnabled = enabled)
    }

    object Idle : BaseWiFiUiState() {
        override fun reduce(uiState: RadioSetUiState): RadioSetUiState = uiState
    }

    object On : BaseWiFiUiState()
    object Off : BaseWiFiUiState() {
        override val enabled = false
    }

    data class DevicesFound(val wifiDevices: List<WifiDevice>) : BaseWiFiUiState() {
        override fun reduce(uiState: RadioSetUiState): RadioSetUiState =
            uiState.copy(devices = wifiDevices)
    }

    class ConnectionFailed(private val message: String) : BaseWiFiUiState() {
        override fun produceEvent(): Event = Event.Toast(message)
    }

    data class Connected(private val device: WifiDevice) : BaseWiFiUiState() {
        override fun produceEvent(): Event = Event.ToDeviceNavigation(device)
    }

    class Disconnected(private val message: String) : BaseWiFiUiState() {
        override fun produceEvent(): Event = Event.Toast(message)
    }
}