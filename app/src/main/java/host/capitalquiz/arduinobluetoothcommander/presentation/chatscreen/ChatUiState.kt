package host.capitalquiz.arduinobluetoothcommander.presentation.chatscreen

data class ChatUiState(
    val connectedDeviceName: String,
    val messages: List<MessageUi> = emptyList(),
    val isConnected: Boolean = true,
)
