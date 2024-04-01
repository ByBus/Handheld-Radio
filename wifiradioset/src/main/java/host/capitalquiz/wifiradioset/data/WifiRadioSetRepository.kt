package host.capitalquiz.wifiradioset.data

import host.capitalquiz.wifiradioset.domain.RadioSetRepository
import host.capitalquiz.wifiradioset.domain.WifiDevice
import host.capitalquiz.wifiradioset.domain.WifiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class WifiRadioSetRepository @Inject constructor(
    private val connectionManager: ConnectionManager,
    private val locationStateDataSource: StateDataSource<LocationState>,
) : RadioSetRepository {
    override val wifiState: Flow<WifiState> = combine(
        connectionManager.state,
        locationStateDataSource.state
    ) { wifiState, locationState ->
        if (locationState.isEnabled()) wifiState else WifiState.Off
    }

    init {
        connectionManager.init()
        locationStateDataSource.init()
    }

    override fun startDeviceDiscovering() = connectionManager.discoverDevices()

    override fun connect(device: WifiDevice) = connectionManager.connect(device)
    override fun disconnect() = connectionManager.disconnect()

    override fun close() {
        connectionManager.close()
        locationStateDataSource.close()
    }
}