package host.capitalquiz.bluetoothchat.data.communication

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import host.capitalquiz.bluetoothchat.data.toDevice
import host.capitalquiz.bluetoothchat.domain.ConnectionError
import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.common.catchException
import host.capitalquiz.common.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

interface Server : SocketHolder {
    fun start(serverName: String, sdpRecord: UUID, timeoutMs: Int): Flow<ConnectionResult>

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
            adapter?.cancelDiscovery()
            val serverSocket = adapter?.listenUsingRfcommWithServiceRecord(
                serverName,
                sdpRecord
            )
            socket = try {
                serverSocket?.accept(timeoutMs)
            } catch (e: Exception) {
                emit(ConnectionError.Error(e.message.toString()))
                null
            } finally {
                closeServerSocket()
            }
            socket?.let { socket ->
                val device = socket.remoteDevice.toDevice()
                emit(ConnectionResult.Connect(device))
            } ?: emit(ConnectionError.Timeout)
        }.flowOn(dispatcher)

        override fun init() = Unit

        private fun closeServerSocket() {
            catchException<IOException> {
                serverSocket?.close()
                serverSocket = null
            }
        }

        override fun close() {
            closeServerSocket()
            catchException<IOException> {
                socket?.close()
                socket = null
            }
        }
    }
}

