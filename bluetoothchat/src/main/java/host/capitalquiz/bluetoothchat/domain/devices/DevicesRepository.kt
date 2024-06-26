package host.capitalquiz.bluetoothchat.domain.devices

import kotlinx.coroutines.flow.StateFlow

interface DevicesRepository {
    val myDeviceName: String
    val pairedDevices: StateFlow<List<Device>>
    val scannedDevices: StateFlow<List<Device>>
    fun discoverDevices()
    fun stopDiscoveringDevices()
}