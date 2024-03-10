package host.capitalquiz.arduinobluetoothcommander.presentation

import host.capitalquiz.arduinobluetoothcommander.domain.Device
import host.capitalquiz.arduinobluetoothcommander.presentation.devicesscreen.BluetoothUiState

interface ConnectionResultUi {
    fun reduce(oldState: BluetoothUiState, toastConsumer: (String) -> Unit): BluetoothUiState

    abstract class Base : ConnectionResultUi {
        protected open val connected = false
        protected open val connecting = false
        protected open val timeout = 0
        protected open val message: String? = null
        protected open val device: Device? = null

        override fun reduce(
            oldState: BluetoothUiState,
            toastConsumer: (String) -> Unit,
        ): BluetoothUiState =
            oldState.copy(
                isConnected = connected,
                isConnecting = connecting,
                showProgressDuration = timeout,
                device = device
            ).also {
                message?.let(toastConsumer::invoke)
            }

    }

    object Idle : Base() {
        override fun reduce(oldState: BluetoothUiState, toastConsumer: (String) -> Unit) = oldState
    }

    data class Connecting(override val timeout: Int) : Base() {
        override val connecting = true
    }

    object ConnectionEstablished : Base() {
        override val connected = true
    }

    class DeviceConnected(override val device: Device, endMessage: String) : Base() {
        override val connected = true
        override val message = "${device.deviceName} $endMessage"
    }

    class DeviceDisconnected(override val device: Device, endMessage: String) : Base() {
        override val connected = false
        override val message = "${device.deviceName} $endMessage"
    }

    class Error(override val message: String) : Base()
}