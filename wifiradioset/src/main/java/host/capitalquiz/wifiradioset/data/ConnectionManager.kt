package host.capitalquiz.wifiradioset.data

import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.domain.WifiState
import kotlinx.coroutines.flow.StateFlow

interface ConnectionManager {
    val wifiState: StateFlow<WifiState>
    fun init()
    fun close()
    fun connect(device: WifiDevice)
    fun disconnect()
    fun discoverDevices()
    fun stopDiscoverDevices()
}