package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.common.di.DispatcherIO
import host.capitalquiz.wifiradioset.domain.WiFiConnectionResult
import host.capitalquiz.wifiradioset.domain.WifiDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

interface RadioSetClient : RadioSetSocketHolder {

    class WiFiClient @Inject constructor(
        private val connectedTo: WifiDevice,
        private val address: InetAddress,
        private val port: Int,
        @DispatcherIO
        private val dispatcher: CoroutineDispatcher,
    ) : RadioSetClient {
        private val socket = Socket()

        override fun connect(): Flow<WiFiConnectionResult> = flow {
            try {
                socket.bind(null)
                socket.connect(InetSocketAddress(address, port), 4000)
                emit(WiFiConnectionResult.Connect(connectedTo))
            } catch (e: IOException) {
                emit(WiFiConnectionResult.Disconnect(connectedTo))
                close()
            }
        }.flowOn(dispatcher)

        override fun close() {
            try {
                socket.close()
            } catch (_: IOException) {
            }
        }

        override fun socket(): Socket = socket
    }

}