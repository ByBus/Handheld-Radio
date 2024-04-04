package host.capitalquiz.wifiradioset.data.devices

import host.capitalquiz.common.data.Closable
import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.domain.WifiState

interface ConnectionManager : StateDataSource<WifiState>, Closable {
    fun connect(device: WifiDevice)
    fun disconnect()
    fun discoverDevices()
    fun stopDiscoverDevices()
}