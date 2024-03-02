package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class DevicesCommunication @Inject constructor(
    private val modeFactory: ConnectionModeFactory,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : Communication {
    private var connectionResultJob: Job? = null
    private val scope = CoroutineScope(dispatcher)
    private val _connectionState = MutableStateFlow<ConnectionResult>(ConnectionResult.Idle)
    override val connectionState = _connectionState.asStateFlow()

    private var currentMode: SocketHolder = SocketHolder.EMPTY

    override suspend fun startServer(serverName: String) {
        val server = modeFactory.createServer()
        setMode(server)
        updateConnectionResult(server.start(serverName, commonDeviceUUID))
    }

    override suspend fun connectToDevice(device: Device) {
        val client = modeFactory.createClient()
        setMode(client)
        updateConnectionResult(client.connect(device, commonDeviceUUID))
    }

    private fun setMode(socketHolder: SocketHolder) {
        _connectionState.tryEmit(ConnectionResult.Connecting())
        currentMode.close()
        currentMode = socketHolder
    }

    private fun updateConnectionResult(connectionResult: Flow<ConnectionResult>) {
        connectionResultJob?.cancel()
        connectionResultJob = scope.launch {
            connectionResult.collect(_connectionState::tryEmit)
        }
    }

    override fun close() {
        scope.cancel()
        currentMode.close()
    }

    companion object {
        private val commonDeviceUUID = UUID.fromString("f81734eb-6c8a-4582-91aa-deff7f3e7268")

    }
}