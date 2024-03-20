package host.capitalquiz.arduinobluetoothcommander.data.communication

import android.annotation.SuppressLint
import host.capitalquiz.arduinobluetoothcommander.data.toDevice
import host.capitalquiz.arduinobluetoothcommander.domain.BluetoothMessage
import host.capitalquiz.arduinobluetoothcommander.domain.Communication
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import host.capitalquiz.common.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

private const val SERVER_CONNECTION_TIMEOUT_MS = 30_000
private const val CLIENT_CONNECTION_TIMEOUT_MS = 10_000

class DevicesCommunication @Inject constructor(
    private val modeFactory: ConnectionModeFactory,
    private val decoder: BluetoothMessageDecoder,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : Communication {
    private var connectionResultJob: Job? = null
    private var scope: CoroutineScope? = null
    private val _connectionState = MutableStateFlow<ConnectionResult>(ConnectionResult.Idle)
    override val connectionState = _connectionState.asStateFlow()

    private var currentMode: SocketHolder = SocketHolder.EMPTY

    override suspend fun startServer(serverName: String) {
        val server = modeFactory.createServer()
        setMode(server, SERVER_CONNECTION_TIMEOUT_MS)
        val connectionResult =
            server.start(serverName, commonDeviceUUID, SERVER_CONNECTION_TIMEOUT_MS)
        updateConnectionResult(connectionResult)
    }

    override suspend fun connectToDevice(device: Device) {
        val client = modeFactory.createClient()
        setMode(client, CLIENT_CONNECTION_TIMEOUT_MS)
        val connectionResult =
            client.connect(device, commonDeviceUUID, CLIENT_CONNECTION_TIMEOUT_MS)
        updateConnectionResult(connectionResult)
    }

    private fun setMode(socketHolder: SocketHolder, connectionDuration: Int) {
        _connectionState.tryEmit(ConnectionResult.Connecting(connectionDuration))
        currentMode.close()
        currentMode = socketHolder
    }

    private fun updateConnectionResult(connectionResult: Flow<ConnectionResult>) {
        if (scope == null) scope = CoroutineScope(dispatcher)
        connectionResultJob?.cancel()
        connectionResultJob = scope?.launch {
            connectionResult.collect(_connectionState::tryEmit)
        }
    }

    @SuppressLint("MissingPermission")
    override fun receive(): Flow<BluetoothMessage> {
        return flow {
            delay(1000)
            if (currentMode.socket?.isConnected != true) return@flow
            val connectedDevice = currentMode.socket?.remoteDevice!!.toDevice()
            val buffer = ByteArray(1024)
            val inputStream = currentMode.socket!!.inputStream
            while (true) {
                try {
                    inputStream.read(buffer)
                } catch (e: Exception) {
                    _connectionState.tryEmit(
                        ConnectionResult.Disconnect(connectedDevice)
                    )
                    break
                }
                emit(decoder.decode(buffer))
            }
        }.flowOn(dispatcher)
    }

    override suspend fun send(message: BluetoothMessage): Boolean {
        return withContext(dispatcher) {
            try {
                val bytes = message.map(decoder)
                currentMode.socket?.outputStream?.write(bytes)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    override fun close() {
        _connectionState.update { connection ->
            connection.abortErrorOrSelf()
        }
        scope?.cancel()
        scope = null
        connectionResultJob = null
        currentMode.close()
    }

    companion object {
        private val commonDeviceUUID = UUID.fromString("f81734eb-6c8a-4582-91aa-deff7f3e7268")
    }
}