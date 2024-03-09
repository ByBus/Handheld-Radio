package host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen

import host.capitalquiz.arduinobluetoothcommander.domain.Device
import host.capitalquiz.arduinobluetoothcommander.presentation.ConnectionResultUi


data class BluetoothUiState(
    val pairedDevices: List<DeviceUi> = emptyList(),
    val scannedDevices: List<DeviceUi> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val showProgressDuration: Long = 0L,
    private val toastMessage: String? = null,
    private val connectedDevice: Device? = null,
    val isDiscoveringDevices: Boolean = false,
) {
    operator fun plus(connectionResultUi: ConnectionResultUi): BluetoothUiState {
        return connectionResultUi.reduce(this)
    }

    fun message(block: (String) -> Unit) = toastMessage?.let(block)

    fun deviceName(block: (String) -> Unit) = connectedDevice?.deviceName?.let(block)
}
