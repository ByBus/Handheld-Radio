package host.capitalquiz.arduinobluetoothcommander.data.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

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

            socket?.let { clientSocket ->
                try {
                    connectionWatcher.listenForConnectionResult(listener)
                    connectionWatcher.watchFor(device)
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
        socket?.close()
        socket = null
        connectionWatcher.close()
    }
}