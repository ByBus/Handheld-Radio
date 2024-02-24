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
                errorMessage = errorMessage
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

    class DeviceConnected(private val device: Device) : Base() {
        override val connected = true
    }

    class DeviceDisconnected(private val device: Device) : Base() {
        override val connected = false
    }

    class Error(override val errorMessage: String) : Base()
}