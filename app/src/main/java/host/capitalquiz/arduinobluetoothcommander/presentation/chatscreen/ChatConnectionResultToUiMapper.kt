package host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen

import host.capitalquiz.arduinobluetoothcommander.domain.ConnectionResult
import javax.inject.Inject

class ChatConnectionResultToUiMapper @Inject constructor() :
    ConnectionResult.Mapper<ChatConnectionUi> {
    override fun invoke(connectionResult: ConnectionResult): ChatConnectionUi {
        return if (connectionResult is ConnectionResult.Disconnect && connectionResult.device.deviceName != null) {
            ChatConnectionUi.Disconnected(connectionResult.device.deviceName)
        } else ChatConnectionUi.Connected
    }
}