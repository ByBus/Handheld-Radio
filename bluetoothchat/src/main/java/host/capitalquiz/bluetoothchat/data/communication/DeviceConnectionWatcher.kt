package host.capitalquiz.bluetoothchat.data.communication

import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.devices.Device
import host.capitalquiz.common.data.Closable

interface DeviceConnectionWatcher : Closable {
    fun listenForConnectionResult(callback: ((ConnectionResult) -> Unit)?)
    fun watchFor(device: Device)
}