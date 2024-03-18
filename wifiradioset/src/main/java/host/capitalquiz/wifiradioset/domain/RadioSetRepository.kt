package host.capitalquiz.wifiradioset.domain

import kotlinx.coroutines.flow.StateFlow

interface RadioSetRepository {
    val devices: StateFlow<List<WifiDevice>>
    val wifiState: StateFlow<WifiState>
    fun startDeviceDiscovering()
    fun connect(device: WifiDevice)
    fun close()
}