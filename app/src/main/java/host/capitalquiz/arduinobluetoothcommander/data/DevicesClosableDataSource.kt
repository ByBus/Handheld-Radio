package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.flow.StateFlow

interface DevicesClosableDataSource : Closable {
    val foundDevices: StateFlow<List<Device>>
}