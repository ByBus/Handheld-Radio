package host.capitalquiz.arduinobluetoothcommander.presentation

import host.capitalquiz.arduinobluetoothcommander.domain.Device
import host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen.BluetoothUiState

interface ConnectionResultUi {
    fun reduce(bluetoothUiState: BluetoothUiState): BluetoothUiState

    abstract class Base : ConnectionResultUi {
        protected open val connected = false
        protected open val connecting = false
        protected open val device: Device? = null
        protected open val timeout = 0L
        protected open val errorMessage: String? = null

        override fun reduce(bluetoothUiState: BluetoothUiState): BluetoothUiState =
            bluetoothUiState.copy(
                isConnected = connected,
                isConnecting = connecting,
                toastMessage = errorMessage,
                connectedDevice = device,
                showProgressDuration = timeout
            )
    }

    object Idle : Base() {
        override fun reduce(bluetoothUiState: BluetoothUiState) = bluetoothUiState
    }

    data class Connecting(override val timeout: Long) : Base() {
        override val connecting = true
    }

    object ConnectionEstablished : Base() {
        override val connected = true
    }

    class DeviceConnected(override val device: Device, endMessage: String) : Base() {
        override val connected = true
        override val errorMessage = "${device.deviceName} $endMessage"
    }

    class DeviceDisconnected(override val device: Device, endMessage: String) : Base() {
        override val connected = false
        override val errorMessage = "${device.deviceName} $endMessage"
    }

    class Error(override val errorMessage: String) : Base()
}