package host.capitalquiz.bluetoothchat.data.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import host.capitalquiz.bluetoothchat.domain.ConnectionError
import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.Device
import host.capitalquiz.common.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds


@SuppressLint("MissingPermission")
class BluetoothClient @Inject constructor(
    private val bluetoothManager: BluetoothManager?,
    private val connectionWatcher: DeviceConnectionWatcher,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : Client {
    private val adapter get() = bluetoothManager?.adapter
    override var socket: BluetoothSocket? = null

    @OptIn(FlowPreview::class)
    override fun connect(device: Device, sdpRecord: UUID, timeoutMs: Int): Flow<ConnectionResult> =
        callbackFlow {
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

                socket?.let { clientSocket ->
                    try {
                        connectionWatcher.watchFor(device)
                        connectionWatcher.listenForConnectionResult(listener)
                        clientSocket.connect()
                    } catch (e: IOException) {
                        trySend(ConnectionError.SocketBusy)
                        close()
                    }
                }
            }
            awaitClose {
                connectionWatcher.listenForConnectionResult(null)
            }
        }.timeout(timeoutMs.milliseconds).catch {
            if (socket?.isConnected != true && it is TimeoutCancellationException) {
                emit(ConnectionError.Timeout)
            }
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