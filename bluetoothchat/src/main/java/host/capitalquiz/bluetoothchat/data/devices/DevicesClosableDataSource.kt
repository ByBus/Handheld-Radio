package host.capitalquiz.bluetoothchat.data.devices

import host.capitalquiz.bluetoothchat.domain.Device
import host.capitalquiz.common.data.Closable
import kotlinx.coroutines.flow.StateFlow

interface DevicesClosableDataSource : Closable {
    val foundDevices: StateFlow<List<Device>>
}