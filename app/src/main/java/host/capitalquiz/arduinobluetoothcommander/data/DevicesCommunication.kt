package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.domain.Client
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import host.capitalquiz.arduinobluetoothcommander.domain.Server
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.merge
import java.util.UUID
import javax.inject.Inject

class DevicesCommunication @Inject constructor(
    private val server: Server,
    private val client: Client,
) : Communication {
    private val commonDeviceUUID = UUID.fromString("f81734eb-6c8a-4582-91aa-deff7f3e7268")

    private val _connectionState = MutableStateFlow<ConnectionResult>(ConnectionResult.Idle)
    override val connectionState =
        merge(server.connectionState, client.connectionState, _connectionState)

    override suspend fun startServer(serverName: String) {
        _connectionState.tryEmit(ConnectionResult.Connecting())
        server.start(serverName, commonDeviceUUID)
    }

    override suspend fun connectToDevice(device: Device) {
        _connectionState.tryEmit(ConnectionResult.Connecting())
        client.connect(device, commonDeviceUUID)
    }

    override fun close() {
        server.disconnect()
        client.close()
    }

    private fun interface Callback {
        fun consumeConnectionResult(result: ConnectionResult)
    }
}