package host.capitalquiz.wifiradioset.domain

sealed interface WiFiConnectionResult {
    object Idle : WiFiConnectionResult
    data class Error(val message: String) : WiFiConnectionResult
    class Connect(device: WifiDevice) : WiFiConnectionResult
    class Disconnect(device: WifiDevice) : WiFiConnectionResult
    object Abort : WiFiConnectionResult
}