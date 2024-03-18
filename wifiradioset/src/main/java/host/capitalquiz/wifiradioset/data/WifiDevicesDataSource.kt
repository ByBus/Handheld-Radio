package host.capitalquiz.wifiradioset.data

import host.capitalquiz.wifiradioset.domain.WifiDevice
import kotlinx.coroutines.flow.StateFlow

interface WifiDevicesDataSource {
    val devices: StateFlow<List<WifiDevice>>
}