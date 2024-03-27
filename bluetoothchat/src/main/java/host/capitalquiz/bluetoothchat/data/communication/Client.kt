package host.capitalquiz.bluetoothchat.data.communication

import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.domain.Device
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Client : SocketHolder {
    fun connect(device: Device, sdpRecord: UUID, timeoutMs: Int): Flow<ConnectionResult>
}