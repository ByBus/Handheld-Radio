package host.capitalquiz.arduinobluetoothcommander.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.Client
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.CoroutineDispatcher
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
    private var clientSocket: BluetoothSocket? = null
    override val connectionState = connectionWatcher.connectionState

    override suspend fun connect(device: Device, sdpRecord: UUID) {
        init()
        withContext(dispatcher) {
            adapter?.cancelDiscovery()
            val btDevice = adapter?.getRemoteDevice(device.mac)

            clientSocket = btDevice
                ?.createRfcommSocketToServiceRecord(sdpRecord)

            clientSocket?.let { socket ->
                try {
                    connectionWatcher.watchFor(device)
                    socket.connect()
                } catch (e: IOException) {
                    socket.close()
                    clientSocket = null
                }
            }
        }
    }

    override fun init() {
        connectionWatcher.init()
    }

    override fun close() {
        clientSocket?.close()
        clientSocket = null
        connectionWatcher.close()
    }
}