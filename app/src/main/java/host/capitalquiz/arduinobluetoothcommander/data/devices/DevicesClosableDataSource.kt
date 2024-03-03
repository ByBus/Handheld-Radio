package host.capitalquiz.arduinobluetoothcommander.data.devices

import host.capitalquiz.arduinobluetoothcommander.data.Closable
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.flow.StateFlow

interface DevicesClosableDataSource : Closable {
    val foundDevices: StateFlow<List<Device>>
}