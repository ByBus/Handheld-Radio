package host.capitalquiz.bluetoothchat.domain

import host.capitalquiz.bluetoothchat.domain.chat.BluetoothMessage
import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.devices.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Communication {
    val connectionState: StateFlow<ConnectionResult>

    suspend fun startServer(serverName: String)

    suspend fun connectToDevice(device: Device)

    fun receive(): Flow<BluetoothMessage>

    suspend fun send(message: BluetoothMessage): Boolean

    fun close()
}