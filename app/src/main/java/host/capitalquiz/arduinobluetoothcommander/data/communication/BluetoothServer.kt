package host.capitalquiz.arduinobluetoothcommander.data.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import host.capitalquiz.arduinobluetoothcommander.data.toDevice
import host.capitalquiz.arduinobluetoothcommander.di.DispatcherIO
import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    override fun start(
        serverName: String,
        sdpRecord: UUID,
        timeoutMs: Int,
    ): Flow<ConnectionResult> = flow {
        val serverSocket = adapter?.listenUsingRfcommWithServiceRecord(
            serverName,
            sdpRecord
        )
        socket = try {
            serverSocket?.accept(timeoutMs)
        } catch (e: Exception) {
            emit(ConnectionResult.Error(e.message.toString()))
            null
        } finally {
            closeServerSocket()
        }
        socket?.let { socket ->
            val device = socket.remoteDevice.toDevice()
            emit(ConnectionResult.Connect(device))
        } ?: emit(ConnectionResult.Error("Timeout exceeded"))
    }.flowOn(dispatcher)

    override fun init() = Unit

    private fun closeServerSocket() {
        try {
            serverSocket?.close()
            serverSocket = null
        } catch (_: IOException) {
        }
    }

    override fun close() {
        try {
            socket?.close()
            socket = null
        } catch (_: IOException) {
        }
        closeServerSocket()
    }
}