package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.flow.StateFlow

interface DeviceConnectionWatcher : Closable {
    val connectionState: StateFlow<ConnectionResult>
    fun watchFor(device: Device)
}