package host.capitalquiz.arduinobluetoothcommander.data.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
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

//            launch {
//                Log.d("BluetoothClient", "connect: before timeout delay ${this@BluetoothClient}")
//                delay(timeoutMs.toLong())
//                Log.d("BluetoothClient", "connect: timeout  ${this@BluetoothClient}")
//                if (socket?.isConnected != true) {
//                    trySend(ConnectionResult.Error("Timeout exceeded"))
//                    close()
//                }
//            }
                var wasConnected = false
                socket?.let { clientSocket ->
                    try {
                        connectionWatcher.watchFor(device)
                        connectionWatcher.listenForConnectionResult(listener)
                        clientSocket.connect()
                        wasConnected = true
                    } catch (e: IOException) {
                        if (wasConnected.not())
                            trySend(ConnectionResult.Error("Please try again after few seconds"))
                        close()
                    }
                }
            }
            awaitClose {
                connectionWatcher.listenForConnectionResult(null)
            }
        }.timeout(timeoutMs.milliseconds).catch {
            if (socket?.isConnected != true && it is TimeoutCancellationException) {
                emit(ConnectionResult.Error("Timeout exceeded"))
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