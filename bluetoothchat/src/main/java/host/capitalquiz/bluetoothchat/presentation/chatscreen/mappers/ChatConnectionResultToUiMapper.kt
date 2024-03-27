package host.capitalquiz.bluetoothchat.presentation.chatscreen.mappers

import host.capitalquiz.bluetoothchat.domain.ConnectionResult
import host.capitalquiz.bluetoothchat.presentation.chatscreen.ChatConnectionUi
import javax.inject.Inject

class ChatConnectionResultToUiMapper @Inject constructor() :
    ConnectionResult.Mapper<ChatConnectionUi> {
    override fun invoke(result: ConnectionResult): ChatConnectionUi {
        return if (result is ConnectionResult.Disconnect && result.device.deviceName != null) {
            ChatConnectionUi.Disconnected(result.device.deviceName)
        } else ChatConnectionUi.Connected
    }
}