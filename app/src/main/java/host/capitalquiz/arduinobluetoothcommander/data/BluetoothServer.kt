package host.capitalquiz.arduinobluetoothcommander.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BluetoothServer @Inject constructor(
    private val bluetoothManager: BluetoothManager?,
    @DispatcherIO
    private val dispatcher: CoroutineDispatcher,
) : Server {

    private var serverSocket: BluetoothServerSocket? = null
    override var socket: BluetoothSocket? = null

    private val adapter get() = bluetoothManager?.adapter

    override fun start(serverName: String, sdpRecord: UUID) = flow {
        val oldName = adapter?.name
        val serverSocket = adapter?.listenUsingRfcommWithServiceRecord(
            serverName,
            sdpRecord
        )
        socket = try {
            adapter?.name = serverName
            serverSocket?.accept(30_000)
        } catch (e: IOException) {
            emit(ConnectionResult.Error(e.message.toString()))
            null
        } finally {
            oldName?.let { adapter?.name = it }
        }

        socket?.let { socket ->
            serverSocket?.close()
            val device = socket.remoteDevice.toDevice()
            emit(ConnectionResult.Connect(device))
        } ?: emit(ConnectionResult.Error("Timeout exceeded"))
    }.onCompletion {
        serverSocket?.close()
        serverSocket = null
    }.flowOn(dispatcher)

    override fun init() = Unit

    override fun close() {
        try {
            serverSocket?.close()
            serverSocket = null
            socket?.close()
            socket = null
        } catch (_: IOException) {

        }
    }
}