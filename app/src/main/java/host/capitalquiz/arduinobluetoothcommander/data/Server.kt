package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Server : SocketHolder {
    fun start(serverName: String, sdpRecord: UUID): Flow<ConnectionResult>
}