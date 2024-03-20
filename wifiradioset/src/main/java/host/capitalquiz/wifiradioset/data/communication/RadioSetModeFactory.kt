package host.capitalquiz.wifiradioset.data.communication

import host.capitalquiz.wifiradioset.domain.WifiDevice
import java.net.InetAddress
import javax.inject.Inject

interface RadioSetModeFactory {

    fun server(connectedTo: WifiDevice, port: Int): RadioSetServer
    fun client(connectedTo: WifiDevice, address: InetAddress, port: Int): RadioSetClient

    class Wifi @Inject constructor() : RadioSetModeFactory {
        override fun server(connectedTo: WifiDevice, port: Int): RadioSetServer {
            return RadioSetServer.WiFiServer(connectedTo, port)
        }

        override fun client(
            connectedTo: WifiDevice,
            address: InetAddress,
            port: Int,
        ): RadioSetClient {
            return RadioSetClient.WiFiClient(connectedTo, address, port)
        }
    }
}