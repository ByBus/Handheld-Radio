package host.capitalquiz.arduinobluetoothcommander.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Server
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BluetoothServer @Inject constructor(
    private val bluetoothManager: BluetoothManager?,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : Server {
    private var connectionJob: Job? = null
    private val connectionScope = CoroutineScope(dispatcher)
    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null
    private val _connectionResult = MutableStateFlow<ConnectionResult>(ConnectionResult.Idle)
    override val connectionState = _connectionResult.asStateFlow()

    private val adapter get() = bluetoothManager?.adapter

    override suspend fun start(serverName: String, sdpRecord: UUID) {
        connectionJob?.cancel()
        connectionJob = connectionScope.launch {
            val oldName = adapter?.name
            val serverSocket = adapter?.listenUsingRfcommWithServiceRecord(
                serverName,
                sdpRecord
            )
            clientSocket = try {
                adapter?.name = serverName
                serverSocket?.accept(30_000)
            } catch (e: IOException) {
                _connectionResult.tryEmit(ConnectionResult.Error(e.message.toString()))
                null
            } finally {
                oldName?.let { adapter?.name = it }
            }
            clientSocket?.let { socket ->
                serverSocket?.close()
                val device = socket.remoteDevice.toDevice()
                _connectionResult.tryEmit(ConnectionResult.Connect(device))
            } ?: _connectionResult.tryEmit(ConnectionResult.Error("Timeout exceeded"))
        }
        connectionJob?.invokeOnCompletion { disconnect() }
    }

    override fun disconnect() {
        connectionJob?.let { job ->
            if (job.isCancelled.not()) job.cancel()
        }
        try {
            serverSocket?.close()
            clientSocket?.close()
            serverSocket = null
            clientSocket = null
        } catch (_: IOException) {

        }
    }
}