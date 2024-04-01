package host.capitalquiz.wifiradioset.domain

import kotlinx.coroutines.flow.Flow

interface RadioSetRepository {
    val wifiState: Flow<WifiState>
    fun startDeviceDiscovering()
    fun connect(device: WifiDevice)
    fun disconnect()
    fun close()
}