package host.capitalquiz.arduinobluetoothcommander.data.communication

import host.capitalquiz.arduinobluetoothcommander.data.Closable
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device

interface DeviceConnectionWatcher : Closable {
    fun listenForConnectionResult(callback: ((ConnectionResult) -> Unit)?)
    fun watchFor(device: Device)
}