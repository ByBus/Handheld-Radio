package host.capitalquiz.wifiradioset.data

import host.capitalquiz.wifiradioset.domain.RadioSetRepository
import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.domain.WifiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class WifiRadioSetRepository @Inject constructor(
    private val connectionManager: ConnectionManager,
) : RadioSetRepository {
    override val devices = connectionManager.devices
    private val _wifiState = MutableStateFlow<WifiState>(WifiState.Idle)
    override val wifiState = _wifiState.asStateFlow()

    init {
        connectionManager.setListener { state ->
            _wifiState.update { state }
        }
        connectionManager.init()
    }

    override fun startDeviceDiscovering() = connectionManager.discoverDevices()

    override fun connect(device: WifiDevice) = connectionManager.connect(device)

    override fun close() = connectionManager.close()
}