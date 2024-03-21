package host.capitalquiz.wifiradioset.data

import host.capitalquiz.wifiradioset.domain.RadioSetRepository
import host.capitalquiz.wifiradioset.domain.WifiDevice
import javax.inject.Inject

class WifiRadioSetRepository @Inject constructor(
    private val connectionManager: ConnectionManager,
) : RadioSetRepository {
    override val wifiState = connectionManager.wifiState

    init {
        connectionManager.init()
    }

    override fun startDeviceDiscovering() = connectionManager.discoverDevices()

    override fun connect(device: WifiDevice) = connectionManager.connect(device)

    override fun close() = connectionManager.close()
}