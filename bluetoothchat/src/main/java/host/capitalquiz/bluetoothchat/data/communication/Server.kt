package host.capitalquiz.bluetoothchat.data.communication

import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Server : SocketHolder {
    fun start(serverName: String, sdpRecord: UUID, timeoutMs: Int): Flow<ConnectionResult>
}