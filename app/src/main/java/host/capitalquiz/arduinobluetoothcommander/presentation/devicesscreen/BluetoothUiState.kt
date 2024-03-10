package host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen

import host.capitalquiz.arduinobluetoothcommander.domain.Device


data class BluetoothUiState(
    val pairedDevices: List<DeviceUi> = emptyList(),
    val scannedDevices: List<DeviceUi> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val showProgressDuration: Int = 0,
    val isDiscoveringDevices: Boolean = false,
    private val device: Device? = null,
) {
    fun deviceData(nameConsumer: (deviceName: String, mac: String) -> Unit) {
        if (device?.deviceName != null) nameConsumer(device.deviceName, device.mac)
    }
}
