package host.capitalquiz.arduinobluetoothcommander.data

import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import host.capitalquiz.arduinobluetoothcommander.domain.Device
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Client : SocketHolder {
    fun connect(device: Device, sdpRecord: UUID): Flow<ConnectionResult>
}