package host.capitalquiz.arduinobluetoothcommander.presentation

import host.capitalquiz.arduinobluetoothcommander.presentation.ui.DeviceUi

data class BluetoothUiState(
    val pairedDevices: List<DeviceUi> = emptyList(),
    val scannedDevices: List<DeviceUi> = emptyList(),
)
