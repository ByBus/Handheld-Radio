package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.flow.StateFlow

interface DevicesClosableDataSource {
    val foundDevices: StateFlow<List<Device>>
    fun init()
    fun close()
}