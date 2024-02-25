package host.capitalquiz.arduinobluetoothcommander.presentation

import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DeviceUi


data class BluetoothUiState(
    val pairedDevices: List<DeviceUi> = emptyList(),
    val scannedDevices: List<DeviceUi> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    private val errorMessage: String? = null,
) {
    operator fun plus(connectionResultUi: ConnectionResultUi): BluetoothUiState {
        return connectionResultUi.reduce(this)
    }

    fun errorMessage(block: (String) -> Unit) = errorMessage?.let(block)
}
