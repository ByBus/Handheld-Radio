package host.capitalquiz.wifiradioset.domain

sealed interface WifiState {
    object Idle : WifiState
    object On : WifiState
    object Off : WifiState
    data class DevicesFound(val wifiDevices: List<WifiDevice>)
    object ConnectionFailed : WifiState
}