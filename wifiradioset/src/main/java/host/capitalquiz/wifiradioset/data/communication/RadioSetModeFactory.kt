package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.common.di.DispatcherIO
import host.capitalquiz.wifiradioset.domain.WifiDevice
import kotlinx.coroutines.CoroutineDispatcher
import java.net.InetAddress
import javax.inject.Inject

interface RadioSetModeFactory {

    fun server(connectedTo: WifiDevice, port: Int): RadioSetServer
    fun client(connectedTo: WifiDevice, address: InetAddress, port: Int): RadioSetClient

    class Wifi @Inject constructor(
        @DispatcherIO
        private val dispatcher: CoroutineDispatcher,
    ) : RadioSetModeFactory {
        override fun server(connectedTo: WifiDevice, port: Int): RadioSetServer {
            return RadioSetServer.WiFiServer(connectedTo, port, dispatcher)
        }

        override fun client(
            connectedTo: WifiDevice,
            address: InetAddress,
            port: Int,
        ): RadioSetClient {
            return RadioSetClient.WiFiClient(connectedTo, address, port, dispatcher)
        }
    }
}