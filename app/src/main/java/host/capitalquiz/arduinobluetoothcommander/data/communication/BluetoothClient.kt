package host.capitalquiz.arduinobluetoothcommander.data.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

private const val CONNECTION_TIMEOUT_MS = 10_000L

@SuppressLint("MissingPermission")
class BluetoothClient @Inject constructor(
    private val bluetoothManager: BluetoothManager?,
    private val connectionWatcher: DeviceConnectionWatcher,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : Client {
    private val adapter get() = bluetoothManager?.adapter
    override var socket: BluetoothSocket? = null

    override fun connect(device: Device, sdpRecord: UUID) = callbackFlow {
        val listener = { result: ConnectionResult ->
            trySend(result)
            Unit
        }
        init()
        withContext(dispatcher) {
            adapter?.cancelDiscovery()
            val btDevice = adapter?.getRemoteDevice(device.mac)

            socket = btDevice
                ?.createRfcommSocketToServiceRecord(sdpRecord)

            launch {
                delay(CONNECTION_TIMEOUT_MS)
                if (socket?.isConnected != true) {
                    trySend(ConnectionResult.Error("Timeout exceeded"))
                    close()
                }
            }

            socket?.let { clientSocket ->
                try {
                    connectionWatcher.watchFor(device)
                    connectionWatcher.listenForConnectionResult(listener)
                    clientSocket.connect()
                } catch (e: IOException) {
                    close()
                }
            }
        }
        awaitClose { connectionWatcher.listenForConnectionResult(null) }
    }.flowOn(dispatcher)

    override fun init() {
        connectionWatcher.init()
    }

    override fun close() {
        try {
            socket?.close()
            socket = null
        } catch (_: IOException) {
        } finally {
            connectionWatcher.close()
        }
    }
}