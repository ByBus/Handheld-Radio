package host.capitalquiz.arduinobluetoothcommander.domain

import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

interface Server {
    val connectionState: SharedFlow<ConnectionResult>
    suspend fun start(serverName: String, sdpRecord: UUID)
    fun disconnect()
}