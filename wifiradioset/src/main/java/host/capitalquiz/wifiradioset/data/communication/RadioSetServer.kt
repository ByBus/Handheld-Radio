package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.domain.WifiDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject

interface RadioSetServer : Connectable, RadioSetSocketHolder {

    class WiFiServer @Inject constructor(
        private val connectedTo: WifiDevice,
        private val port: Int,
    ) : RadioSetServer {
        private var serverSocket = ServerSocket(port)
        private var socket: Socket? = null

        override fun connect(): Flow<WiFiConnectionResult> = flow {
            socket = try {
                serverSocket.accept()
            } catch (e: Exception) {
                emit(WiFiConnectionResult.Disconnect(connectedTo))
                null
            } finally {
                closeServerSocket()
            }
            socket?.let { socket ->
                emit(WiFiConnectionResult.Connect(connectedTo))
            } ?: emit(WiFiConnectionResult.Error("Not connected"))
        }.flowOn(Dispatchers.IO)

        private fun closeServerSocket() {
            try {
                serverSocket.close()
            } catch (_: IOException) {
            }
        }

        override fun close() {
            closeServerSocket()
            try {
                socket?.close()
                socket = null
            } catch (_: IOException) {
            }
        }

        override fun socket(): Socket? = socket
    }
}