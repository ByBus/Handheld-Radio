package host.capitalquiz.wifiradioset.presentation.devices

import host.capitalquiz.wifiradioset.domain.WifiDevice

sealed interface WifiStateUi {
    fun reduce(uiState: RadioSetUiState): RadioSetUiState
    fun produceEvent(): WiFiEvent

    abstract class BaseWiFiUiState : WifiStateUi {
        protected open val enabled = true
        override fun produceEvent(): WiFiEvent = WiFiEvent.Empty

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
        override fun produceEvent(): WiFiEvent = WiFiEvent.Toast(message)
    }

    object Connected : BaseWiFiUiState() {
        override fun produceEvent(): WiFiEvent = WiFiEvent.Navigation
    }

    class Disconnected(private val message: String) : BaseWiFiUiState() {
        override fun produceEvent(): WiFiEvent = WiFiEvent.Toast(message)
    }
}