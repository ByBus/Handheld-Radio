package host.capitalquiz.bluetoothchat.data.devices

import host.capitalquiz.bluetoothchat.data.Closable
import host.capitalquiz.bluetoothchat.domain.Device
import kotlinx.coroutines.flow.StateFlow

interface DevicesClosableDataSource : Closable {
    val foundDevices: StateFlow<List<Device>>
}