package host.capitalquiz.wifiradioset.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.net.InetAddress

interface RadioSetCommunication : CommunicationMode, Communication

interface CommunicationMode {
    fun configureAsServer(connectedDevice: WifiDevice)
    fun configureAsClient(connectedDevice: WifiDevice, address: InetAddress)
}

interface Communication {
    val connectionResult: StateFlow<WiFiConnectionResult>
    fun connect()
    suspend fun send(message: String): Boolean
    fun receive(): Flow<String>
    fun stop()
}