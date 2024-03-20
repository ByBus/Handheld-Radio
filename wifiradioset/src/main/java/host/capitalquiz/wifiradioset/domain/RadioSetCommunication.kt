package host.capitalquiz.wifiradioset.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.net.InetAddress

interface RadioSetCommunication {
    val connectionResult: StateFlow<WiFiConnectionResult>
    fun startServer(connectedDevice: WifiDevice)
    fun startClient(connectedDevice: WifiDevice, address: InetAddress)

    suspend fun send(message: String): Boolean
    fun receive(): Flow<String>
    fun stop()
}