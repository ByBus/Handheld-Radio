package host.capitalquiz.arduinobluetoothcommander.domain

import host.capitalquiz.arduinobluetoothcommander.data.Closable
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

interface Client : Closable {
    val connectionState: SharedFlow<ConnectionResult>
    suspend fun connect(device: Device, sdpRecord: UUID)
}