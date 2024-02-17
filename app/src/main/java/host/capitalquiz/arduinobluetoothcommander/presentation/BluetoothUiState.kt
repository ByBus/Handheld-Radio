package host.capitalquiz.arduinobluetoothcommander.presentation

import host.capitalquiz.arduinobluetoothcommander.domain.Device

data class BluetoothUiState(val pairedDevices: List<Device> = emptyList(), val scannedDevices: List<Device> = emptyList())
