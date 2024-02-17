package host.capitalquiz.arduinobluetoothcommander.domain

import kotlinx.coroutines.flow.StateFlow

interface DevicesRepository {
    val pairedDevices: StateFlow<List<Device>>
    val scannedDevices: StateFlow<List<Device>>
    fun discoverDevices()
    fun stopDiscoveringDevices()
}