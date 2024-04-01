package host.capitalquiz.wifiradioset.data

import host.capitalquiz.common.data.Closable
import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.domain.WifiState
import kotlinx.coroutines.flow.StateFlow

interface ConnectionManager : Closable {
    val wifiState: StateFlow<WifiState>
    fun connect(device: WifiDevice)
    fun disconnect()
    fun discoverDevices()
    fun stopDiscoverDevices()
}