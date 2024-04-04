package host.capitalquiz.wifiradioset.data.communication

import android.util.Log
import host.capitalquiz.common.di.DispatcherIO
import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.domain.WifiDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject
import host.capitalquiz.common.catchException

interface RadioSetServer : RadioSetSocketHolder {

    class WiFiServer @Inject constructor(
        private val connectedTo: WifiDevice,
        port: Int,
        @DispatcherIO
        private val dispatcher: CoroutineDispatcher,
    ) : RadioSetServer {
        private var serverSocket = ServerSocket(port)
        private var socket: Socket? = null

        override fun connect(): Flow<WiFiConnectionResult> = flow {
            socket = try {
                serverSocket.accept()
            } catch (e: Exception) {
                Log.d("WiFiServerError", "connect: ${e.message}")
                emit(WiFiConnectionResult.Disconnect(connectedTo))
                null
            } finally {
                closeServerSocket()
            }
            socket?.let {
                emit(WiFiConnectionResult.Connect(connectedTo))
            } ?: emit(WiFiConnectionResult.Error("Not connected"))
        }.flowOn(dispatcher)

        private fun closeServerSocket() {
            catchException<IOException> {
                serverSocket.close()
            }
        }


        override fun close() {
            closeServerSocket()
            catchException<IOException> {
                socket?.close()
                socket = null
            }
        }

        override fun socket(): Socket? = socket
    }
}