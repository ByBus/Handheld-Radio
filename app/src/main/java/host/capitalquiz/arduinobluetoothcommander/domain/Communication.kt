package host.capitalquiz.arduinobluetoothcommander.domain

import kotlinx.coroutines.flow.Flow

interface Communication {
    val connectionState: Flow<ConnectionResult>

    suspend fun startServer(serverName: String)

    suspend fun connectToDevice(device: Device)
    fun close()
}