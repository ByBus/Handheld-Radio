package host.capitalquiz.wifiradioset.domain

import kotlinx.coroutines.flow.StateFlow
import java.io.InputStream
import java.net.InetAddress

interface RadioSetCommunication : CommunicationMode, Communication

interface CommunicationMode {
    fun configureAsServer(connectedDevice: WifiDevice)
    fun configureAsClient(connectedDevice: WifiDevice, address: InetAddress)
}

interface Communication {
    val connectionResult: StateFlow<WiFiConnectionResult>
    fun connect()
    suspend fun recordAudio()
    suspend fun playAudio()
    suspend fun mute(disableSound: Boolean)
    fun stop()
    suspend fun sendAudio(inputStream: InputStream)
}