package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device

interface DeviceConnectionWatcher : Closable {
    fun listenForConnectionResult(callback: ((ConnectionResult) -> Unit)?)
    fun watchFor(device: Device)
}