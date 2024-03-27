package host.capitalquiz.bluetoothchat.data.communication

import host.capitalquiz.bluetoothchat.data.Closable
import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.Device

interface DeviceConnectionWatcher : Closable {
    fun listenForConnectionResult(callback: ((ConnectionResult) -> Unit)?)
    fun watchFor(device: Device)
}