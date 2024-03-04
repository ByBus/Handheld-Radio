package host.capitalquiz.arduinobluetoothcommander.data.communication

import android.annotation.SuppressLint
import android.util.Log
import host.capitalquiz.arduinobluetoothcommander.data.toDevice
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.BluetoothMessage
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

class DevicesCommunication @Inject constructor(
    private val modeFactory: ConnectionModeFactory,
    private val decoder: BluetoothMessageDecoder,
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
        val connectionResult = server.start(serverName, commonDeviceUUID)
        updateConnectionResult(connectionResult)
    }

    override suspend fun connectToDevice(device: Device) {
        val client = modeFactory.createClient()
        setMode(client)
        val connectionResult = client.connect(device, commonDeviceUUID)
        updateConnectionResult(connectionResult)
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

    @SuppressLint("MissingPermission")
    override fun receive(): Flow<BluetoothMessage> {
        return flow {
            if (currentMode.socket?.isConnected != true) return@flow
            val connectedDevice = currentMode.socket?.remoteDevice!!.toDevice()
            val buffer = ByteArray(1024)
            val inputStream = currentMode.socket!!.inputStream

            ByteArrayOutputStream().use { bos ->
                while (true) {
                    bos.reset()
                    try {
                        var bytesRead: Int
                        while (
                            inputStream.read(buffer, 0, buffer.size).also { bytesRead = it } != -1
                        ) {
                            bos.write(buffer, 0, bytesRead)
                        }
                    } catch (e: IOException) {
                        Log.d("Communication", "Input stream was disconnected", e)
                        _connectionState.tryEmit(
                            ConnectionResult.Disconnect(connectedDevice)
                        )
                        break
                    }
                    emit(decoder.decode(bos.toByteArray()))
                }
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
        scope.cancel()
        currentMode.close()
    }

    companion object {
        private val commonDeviceUUID = UUID.fromString("f81734eb-6c8a-4582-91aa-deff7f3e7268")
    }
}