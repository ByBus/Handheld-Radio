package host.capitalquiz.arduinobluetoothcommander.domain

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