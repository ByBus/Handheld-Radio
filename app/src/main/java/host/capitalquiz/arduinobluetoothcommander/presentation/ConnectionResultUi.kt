package host.capitalquiz.arduinobluetoothcommander.presentation

import host.capitalquiz.arduinobluetoothcommander.domain.Device

interface ConnectionResultUi {
    fun reduce(bluetoothUiState: BluetoothUiState): BluetoothUiState

    abstract class Base : ConnectionResultUi {
        protected open val connected = false
        protected open val connecting = false
        protected open val errorMessage: String? = null
        override fun reduce(bluetoothUiState: BluetoothUiState): BluetoothUiState =
            bluetoothUiState.copy(
                isConnected = connected,
                isConnecting = connecting,
                toastMessage = errorMessage
            )
    }

    object Idle : Base() {
        override fun reduce(bluetoothUiState: BluetoothUiState) = bluetoothUiState
    }

    object Connecting : Base() {
        override val connecting = true
    }

    object ConnectionEstablished : Base() {
        override val connected = true
    }

    class DeviceConnected(device: Device, endMessage: String) : Base() {
        override val connected = true
        override val errorMessage = "${device.deviceName} $endMessage"
    }

    class DeviceDisconnected(device: Device, endMessage: String) : Base() {
        override val connected = false
        override val errorMessage = "${device.deviceName} $endMessage"
    }

    class Error(override val errorMessage: String) : Base()
}