package host.capitalquiz.wifiradioset.data

import host.capitalquiz.wifiradioset.domain.WifiDevice

interface ConnectionManager : WifiDevicesDataSource {
    fun init()
    fun close()
    fun connect(device: WifiDevice)
    fun discoverDevices()
    fun stopDiscoverDevices()
    fun setListener(listener: WifiConnectionManager.Listener)
}